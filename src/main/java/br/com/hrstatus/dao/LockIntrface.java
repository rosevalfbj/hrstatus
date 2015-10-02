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

package br.com.hrstatus.dao;

import java.util.List;

import br.com.hrstatus.model.Lock;

/*
 * @author spolti
 */

public interface LockIntrface {

	public void insertLock(Lock lock);
	
	public void insertLockScheduler(Lock lock, String schedulerName);
	
	public void removeLock(Lock lock);
	
	public void removeLockScheduler(Lock lock, String schedulerName);
	
	public List<Lock> listLockedServices(String recurso);
	
	public List<Lock> listLockedServicesScheduler(String recurso, String schedulerName);
	
	public List<Lock> ListAllLocks();
	
	public Lock getLockByID(int id);
	
}