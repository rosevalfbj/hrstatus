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

package br.com.ohsnap.hrstatus.dao;

/*
 * @author spolti
 */

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.ohsnap.hrstatus.model.PassExpire;
import br.com.ohsnap.hrstatus.model.Users;

@Repository
@Transactional
public class UsersDAO implements UsersInterface {
	
	private EntityManager entityManager;

	public UsersDAO() {

	}

	@PersistenceContext(unitName = "pu-hr")
	protected final void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	private Session session() {
		return ((Session) entityManager.getDelegate());
	}
	
	public void saveORupdateUser(Users user){
		Logger.getLogger(getClass()).debug("saveORupdateUser() -> Saving or Update User.");
		session().save(user);
	}
	
	@SuppressWarnings("unchecked")
	public List<Users> listUser(){
		Logger.getLogger(getClass()).debug("listUser() -> listing users.");
		return session().createCriteria(Users.class).list();
		
	}
	
	public boolean deleteUserByID(Users user) {
		Logger.getLogger(getClass()).debug("deleteUserByID() -> Deletando usuário.");
		try {
			session().refresh(user);
			session().delete(user);
			return true;
		} catch (Exception e) {

			return false;
		}

	}
	
	public Users getUserByID(String username){
		Logger.getLogger(getClass()).debug("getUserByID() -> Obtendo dados do usuário " + username);
		return (Users) session().createCriteria(Users.class)
				.add(Restrictions.eq("username", username)).uniqueResult();
	}
	
	public void updateUser(Users user){
		Logger.getLogger(getClass()).debug("updateUser() -> Atualizando usuário.");
		session().update(user);
	}
	
	public String getPass(String username){
		Logger.getLogger(getClass()).debug("getPass() -> Buscando senha do usuário " + username);
		Criteria criteria = session().createCriteria(Users.class);
		criteria.add(Restrictions.eq("username", username));
		ProjectionList proList = Projections.projectionList();
		proList.add(Projections.property("password"));
		criteria.setProjection(proList);
		return criteria.uniqueResult().toString();
	}
	
	public int searchUser(String username){
		Logger.getLogger(getClass()).debug("searchUser() -> Buscando usuário " + username);
		Criteria criteria = session().createCriteria(Users.class);
		criteria.add(Restrictions.eq("username", username));
		criteria.setProjection(Projections.rowCount());
		int count = ((Long) criteria.uniqueResult()).intValue();
		return count;
	}
	
	public String getMail (String username){
		Logger.getLogger(getClass()).debug("getMail() -> Obtendo email do usuário " + username);
		Criteria criteria = session().createCriteria(Users.class);
		criteria.add(Restrictions.eq("username", username));
		ProjectionList proList = Projections.projectionList();
		proList.add(Projections.property("mail"));
		criteria.setProjection(proList);
		return criteria.uniqueResult().toString();
	}
	
	public void setExpirePasswordTime(PassExpire passExpire){
		Logger.getLogger(getClass()).debug("saveORupdateUser() -> Saving or Update User.");
		session().save(passExpire);
	}
	
	public int searchUserChangePass(String username){
		Logger.getLogger(getClass()).debug("searchUserChangePass() -> Verificando usuário para gerar nova senha.");
		Criteria criteria = session().createCriteria(PassExpire.class);
		criteria.add(Restrictions.eq("username", username));
		criteria.setProjection(Projections.rowCount());
		int count = ((Long) criteria.uniqueResult()).intValue();
		return count;
	}
	
	@SuppressWarnings("unchecked")
	public List<PassExpire> getExpireTime(){
		Logger.getLogger(getClass()).debug("getExpireTime() -> Buscando valores da tabela de novas senhas.");
		Criteria criteria = session().createCriteria(PassExpire.class);
		return criteria.list();
	}
	
	public void delUserExpireTime(PassExpire passExpire){
		Logger.getLogger(getClass()).debug("delUserExpireTime() -> Deletando usuário " + passExpire.getUsername() + " da tabela temporária.");
		session().refresh(passExpire);
		session().delete(passExpire);
	
	}
}
