/*
    Copyright (C) 2012  Filippe Costa Spolti

    This file is part of Hrstatus.

    Hrstatus is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package br.com.hrstatus.controller;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.validator.ValidationMessage;
import br.com.hrstatus.dao.ServersInterface;
import br.com.hrstatus.dao.UsersInterface;
import br.com.hrstatus.dao.Configuration;
import br.com.hrstatus.dao.BancoDadosInterface;
import br.com.hrstatus.dao.InstallProcessInterface;
import br.com.hrstatus.model.BancoDados;
import br.com.hrstatus.model.Servidores;
import br.com.hrstatus.model.Users;
import br.com.hrstatus.security.Crypto;
import br.com.hrstatus.security.SpringEncoder;
import br.com.hrstatus.utils.GetSystemInformation;
import br.com.hrstatus.utils.PassGenerator;
import br.com.hrstatus.utils.PropertiesLoaderImpl;
import br.com.hrstatus.utils.UserInfo;
import br.com.hrstatus.utils.mail.MailSender;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * @author spolti
 */
@Resource
public class CadastroController {

    Logger log = Logger.getLogger(CadastroController.class.getCanonicalName());

    @Autowired
    private Result result;
    @Autowired
    private ServersInterface iteracoesDAO;
    @Autowired
    private Validator validator;
    @Autowired
    private UsersInterface userDAO;
    @Autowired
    private Configuration configurationDAO;
    @Autowired
    private BancoDadosInterface BancoDadosDAO;
    @Autowired
    private InstallProcessInterface ipi;
    private UserInfo userInfo = new UserInfo();
    private Crypto encodePass = new Crypto();
    private GetSystemInformation getSys = new GetSystemInformation();

    private ArrayList<String> OS = new ArrayList<String>();
    private ArrayList<String> VENDOR = new ArrayList<String>();

    private ArrayList<String> setOSList() {
        OS = new ArrayList<String>();
        OS.add("UNIX");
        OS.add("WINDOWS");
        return OS;
    }

    private ArrayList<String> setDataBaseVendorList() {
        VENDOR.add("MySQL");
        VENDOR.add("ORACLE");
        VENDOR.add("PostgreSQL");
        VENDOR.add("SqlServer");
        VENDOR.add("DB2");
        return VENDOR;
    }

    @SuppressWarnings("static-access")
    @Get("/newServer")
    public void newServer(Servidores servidores) {

        // Inserting HTML title in the result
        result.include("title", "Registrar Servidor");

        //Sending information to "About" page
        final PropertiesLoaderImpl load = new PropertiesLoaderImpl();
        final String version = load.getValor("version");
        result.include("version", version);
        final List<String> info = getSys.SystemInformation();
        result.include("jvmName", info.get(2));
        result.include("jvmVendor", info.get(1));
        result.include("jvmVersion", info.get(0));
        result.include("osInfo", info.get(3));
        result.include("installDate", ipi.getInstallationDate());

        result.include("loggedUser", userInfo.getLoggedUsername());

        log.info("[ " + userInfo.getLoggedUsername() + " ] URI Called: /newServer");
        result.include("servidores", servidores);

        // populating Database combobox
        result.include("OS", setOSList());
    }

    @SuppressWarnings("static-access")
    @Post("/registerServer")
    public void registerServer(Servidores servidores) {

        // Inserting HTML title in the result
        result.include("title", "Registrar Servidor");

        result.include("loggedUser", userInfo.getLoggedUsername());

        log.info("[ " + userInfo.getLoggedUsername() + " ] URI Called: /registerServer");

        // Regex to validade the IP
        final Pattern pattern = Pattern.compile("\\A(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}\\z");
        final Matcher matcher = pattern.matcher(servidores.getIp());

        if (servidores.getIp().isEmpty()) {
            validator.add(new ValidationMessage("O campo Ip deve ser informado", "Erro"));
        } else if (!matcher.matches()) {
            validator.add(new ValidationMessage("O ip " + servidores.getIp() + " não é válido.", "Erro"));
        } else if (servidores.getHostname().isEmpty()) {
            validator.add(new ValidationMessage("O campo Hostname deve ser informado", "Erro"));
        } else if (servidores.getUser().isEmpty()) {
            validator.add(new ValidationMessage("O campo Usuário deve ser informado", "Erro"));
        } else if (servidores.getPass().isEmpty()) {
            validator.add(new ValidationMessage("O campo Senha deve ser informado", "Erro"));
        } else if (servidores.getPort() <= 0 || servidores.getPort() >= 65536) {
            validator.add(new ValidationMessage("O campo porta está incorreto ou vazio", "Erro"));
        } else if (servidores.getSO().isEmpty()) {
            validator.add(new ValidationMessage("O campo SO deve ser informado", "Erro"));
        } else if (servidores.getLogCurrent().isEmpty()) {
        	validator.add(new ValidationMessage("O campo Log Current deve ser informado", "Erro"));
        } else if (servidores.getLogDir().isEmpty()) {
            servidores.setLogDir(null);
        }
        // populating Database combobox
        result.include("OS", setOSList());

        validator.onErrorUsePageOf(CadastroController.class).newServer(servidores);

        result.include("servidores", servidores);

        servidores.setSO(servidores.getSO().toUpperCase());
        servidores.setStatus("Servidor ainda não foi verificado.");
        servidores.setTrClass("error");


        try {
            // Encrypting the password
            servidores.setPass(encodePass.encode(servidores.getPass()));

        } catch (Exception e) {
            log.severe("Error: " + e);
        }

        if (this.iteracoesDAO.insert_server(servidores) == 0) {
            result.include("msg", "Server " + servidores.getHostname() + " was sucessfully registred.");
            log.info("Server " + servidores.getHostname() + " was sucessfully registred.");
            result.redirectTo(ConfigController.class).configClients();
        } else {
            validator.add(new ValidationMessage("Server " + servidores.getHostname() + " was not registred because already exists.", "Erro"));
            validator.onErrorForwardTo(CadastroController.class).newServer(servidores);
        }

    }

    @SuppressWarnings("static-access")
    @Get("/newDataBase")
    public void newDataBase(BancoDados bancoDados) {

        // Inserting HTML title in the result
        result.include("title", "Registrar Banco de Dados");

        //Sending information to "About" page
        final PropertiesLoaderImpl load = new PropertiesLoaderImpl();
        final String version = load.getValor("version");
        result.include("version", version);
        final List<String> info = getSys.SystemInformation();
        result.include("jvmName", info.get(2));
        result.include("jvmVendor", info.get(1));
        result.include("jvmVersion", info.get(0));
        result.include("osInfo", info.get(3));
        result.include("installDate", ipi.getInstallationDate());

        result.include("loggedUser", userInfo.getLoggedUsername());

        log.info("[ " + userInfo.getLoggedUsername() + " ] URI Called: /newDataBase");
        result.include("bancoDados", bancoDados);

        // populating Database combobox
        result.include("VENDOR", setDataBaseVendorList());
    }

    @SuppressWarnings("static-access")
    @Post("/registerDataBase")
    public void registerDataBase(BancoDados bancoDados) {

        // Inserting HTML title in the result
        result.include("title", "Registrar Banco de Dados");

        result.include("loggedUser", userInfo.getLoggedUsername());

        log.info("[ " + userInfo.getLoggedUsername() + " ] URI Called: /registerDataBase");

        // Regex to validade IP
        final Pattern pattern = Pattern.compile("\\A(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}\\z");
        final Matcher matcher = pattern.matcher(bancoDados.getIp());

        if (bancoDados.getIp().isEmpty()) {
            validator.add(new ValidationMessage("O campo Ip deve ser informado", "Erro"));
        } else if (!matcher.matches()) {
            validator.add(new ValidationMessage("O ip " + bancoDados.getIp() + " não é válido.", "Erro"));
        } else if (bancoDados.getHostname().isEmpty()) {
            validator.add(new ValidationMessage("O campo Hostname deve ser informado", "Erro"));
        } else if (bancoDados.getUser().isEmpty()) {
            validator.add(new ValidationMessage("O campo Usuário deve ser informado", "Erro"));
        } else if (bancoDados.getPass().isEmpty()) {
            validator.add(new ValidationMessage("O campo Senha deve ser informado", "Erro"));
        } else if (bancoDados.getPort() <= 0 || bancoDados.getPort() >= 65536) {
            if (bancoDados.getVendor().toUpperCase().equals("MYSQL")) {
                bancoDados.setPort(3306);
            }
            if (bancoDados.getVendor().toUpperCase().equals("ORACLE")) {
                bancoDados.setPort(1521);
            }
            if (bancoDados.getVendor().toUpperCase().equals("SQLSERVER")) {
                bancoDados.setPort(1433);
            }
            if (bancoDados.getVendor().toUpperCase().equals("POSTGRESQL")) {
                bancoDados.setPort(5432);
            }
            if (bancoDados.getVendor().toUpperCase().equals("DB2")) {
                bancoDados.setPort(50000);
            }
        } else if (bancoDados.getVendor().isEmpty()) {
            validator.add(new ValidationMessage("O campo Vendor deve ser informado", "Erro"));
        }
        if (bancoDados.getQueryDate().isEmpty()) {
            if (bancoDados.getVendor().toUpperCase().equals("MYSQL")) {
                bancoDados.setQueryDate("SELECT NOW() AS date;");
            }
            if (bancoDados.getVendor().toUpperCase().equals("ORACLE")) {
                bancoDados.setQueryDate("select sysdate from dual");
            }
            if (bancoDados.getVendor().toUpperCase().equals("SQLSERVER")) {
                bancoDados.setQueryDate("SELECT GETDATE();");
            }
            if (bancoDados.getVendor().toUpperCase().equals("POSTGRESQL")) {
                bancoDados.setQueryDate("SELECT now();");
            }
            if (bancoDados.getVendor().toUpperCase().equals("DB2")) {
                bancoDados.setQueryDate("select VARCHAR_FORMAT(CURRENT_TIMESTAMP, 'YYYY-MM-DD HH24:MM:SS') FROM SYSIBM.SYSDUMMY1");
            }
        }

        // populating Database vendor combobox
        result.include("VENDOR", setDataBaseVendorList());

        validator.onErrorUsePageOf(CadastroController.class).newDataBase(bancoDados);

        result.include("bancoDados", bancoDados);

        bancoDados.setVendor(bancoDados.getVendor().toUpperCase());
        bancoDados.setStatus("NOK");
        bancoDados.setTrClass("error");

        try {
            // Encrypting the password
            bancoDados.setPass(encodePass.encode(bancoDados.getPass()));
        } catch (Exception e) {
            log.severe("Error: " + e);
        }

        if (this.BancoDadosDAO.insert_dataBase(bancoDados) == 0) {
            result.include("msg", "BancoDados " + bancoDados.getHostname() + " was sucessfully registred.");
            log.info("DataBase " + bancoDados.getHostname() + " was sucessfully registred.");
            result.redirectTo(ConfigController.class).configDataBases();
        } else {
            validator.add(new ValidationMessage("DataBase " + bancoDados.getHostname() + " was not registred because it already exists.", "Erro"));
            validator.onErrorForwardTo(CadastroController.class).newDataBase(bancoDados);
        }
    }

    @SuppressWarnings("static-access")
    @Get("/newUser")
    public void newUser(Users user) {

        // Inserting HTML title in the result
        result.include("title", "Registrar Usuário");
        result.include("loggedUser", userInfo.getLoggedUsername());

        //Sending information to "About" page
        final PropertiesLoaderImpl load = new PropertiesLoaderImpl();
        final String version = load.getValor("version");
        result.include("version", version);
        final List<String> info = getSys.SystemInformation();
        result.include("jvmName", info.get(2));
        result.include("jvmVendor", info.get(1));
        result.include("jvmVersion", info.get(0));
        result.include("osInfo", info.get(3));
        result.include("installDate", ipi.getInstallationDate());

        final int count = iteracoesDAO.countServerWithLog();
        final List<Servidores> server = this.iteracoesDAO.getHostnamesWithLogDir();

        log.info("[ " + userInfo.getLoggedUsername() + " ] URI Called: /newUser");
        result.include("user", user);
        result.include("count", count);
        result.include("server", server);

    }

    @SuppressWarnings("static-access")
    @Post("/registerUser")
    public void registerUser(Users user, String[] idServer, boolean checkall) throws UnsupportedEncodingException, UnknownHostException {

        // Inserting HTML title in the result
        result.include("title", "Registrar Usuário");
        result.include("loggedUser", userInfo.getLoggedUsername());
        List<Servidores> idAccessServers = new ArrayList<Servidores>();

        log.fine("[ " + userInfo.getLoggedUsername() + " ]URI Called: /registerUser");
        final SpringEncoder encode = new SpringEncoder();

        // Regex to e-mail validation
        final Pattern p = Pattern.compile("^[\\w-]+(\\.[\\w-]+)*@([\\w-]+\\.)+[a-zA-Z]{2,7}$");
        final Matcher m = p.matcher(user.getMail());

        //getting servers with logDir configured
        final List<Servidores> server = this.iteracoesDAO.getHostnamesWithLogDir();

        if (user.getNome().isEmpty()) {
            result.include("server", server);
            validator.add(new ValidationMessage("O campo Nome deve ser informado", "Erro"));
        } else if (user.getUsername().isEmpty()) {
            result.include("server", server);
            validator.add(new ValidationMessage("O campo Username deve ser informado", "Erro"));
        } else if (user.getPassword().isEmpty() && user.getConfirmPass().isEmpty()) {
            final PassGenerator gemPass = new PassGenerator();
            final String password = gemPass.gemPass();
            user.setPassword(password);
            log.info("[ " + userInfo.getLoggedUsername() + " ] - Senha gerada");
        } else if (!user.getPassword().equals(user.getConfirmPass())) {
            result.include("server", server);
            validator.add(new ValidationMessage("As senhas informadas não são iguais.", "Erro"));
        } else if (user.getPassword().equals(user.getConfirmPass())) {

            //Verifying the password complexity
            final List<String> passVal = new ArrayList<String>();
            Map<String, String> map = new HashMap<String, String>();
            map = br.com.hrstatus.security.PasswordPolicy.verifyPassComplexity(user.getPassword());
            final Object[] valueMap = map.keySet().toArray();
            for (int i = 0; i < valueMap.length; i++) {
                if (map.get(valueMap[i]).equals("false")) {
                    passVal.add(map.get(valueMap[i + 1]));
                }
            }
            for (int j = 0; j < passVal.size(); j++) {
                validator.add(new ValidationMessage(passVal.get(j), "Erro"));
            }

            result.include("server", server);

        } else if (user.getMail().isEmpty()) {
            result.include("server", server);
            validator.add(new ValidationMessage("O campo E-mail deve ser informado", "Erro"));
        } else if (!m.find()) {
            result.include("server", server);
            validator.add(new ValidationMessage("Favor informe o e-mail corretamente.", "Erro"));
        } else if (user.getAuthority().isEmpty()) {
            result.include("server", server);
            validator.add(new ValidationMessage("O campo Perfil deve ser informado", "Erro"));

        }

        if (checkall) {
            log.fine("[ " + userInfo.getLoggedUsername() + " ] The checkbox select all server is checked.");
            idAccessServers = this.iteracoesDAO.getHostnamesWithLogDir();
            user.setServer(idAccessServers);

        } else if (!checkall && idServer != null) {
            for (int i = 0; i < idServer.length; i++) {
                if (!idServer[i].equals("notNull")) {
                    idAccessServers.add(this.iteracoesDAO.getServerByID(Integer.parseInt(idServer[i])));
                    log.fine("Server ID received: " + idServer[i]);
                }
            }
            user.setServer(idAccessServers);
            for (Servidores sv : user.getServer()) {
                log.fine("*******************server " + sv.getHostname());
            }
        }
        result.include("user", user);
        validator.onErrorUsePageOf(CadastroController.class).newUser(user);

        //new user, setting firstLogin to true.
        user.setFirstLogin(true);

        // Encrypting the password and save the new user
        // Encrypting the password using the MD5 module of springframework
        user.setPassword(encode.encodePassUser(user.getPassword()));
        this.userDAO.saveORupdateUser(user);

        log.info("[ " + userInfo.getLoggedUsername() + " ] The user " + user.getUsername() + " was succesfully created.");
        // Sending a e-mail to the user to notify about the user creation.
        final MailSender sendMail = new MailSender();
        sendMail.sendCreatUserInfo(this.configurationDAO.getMailSender(),
                user.getMail(), this.configurationDAO.getJndiMail(),
                user.getNome(), user.getUsername(), user.getPassword());
        result.redirectTo(HomeController.class).home("null");
    }
}