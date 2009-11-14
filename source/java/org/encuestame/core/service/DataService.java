/**
 * encuestame:  system online surveys
 * Copyright (C) 2005-2008 encuestame Development Team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of version 3 of the GNU General Public
 * License as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */
package org.encuestame.core.service;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.encuestame.core.exception.EnMeExpcetion;
import org.encuestame.core.persistence.dao.CatLocationDao;
import org.encuestame.core.persistence.dao.CatStateDaoImp;
import org.encuestame.core.persistence.dao.ProjectDaoImp;
import org.encuestame.core.persistence.pojo.CatState;
import org.encuestame.core.persistence.pojo.Project;
import org.encuestame.web.beans.project.UnitProjectBean;
import org.hibernate.HibernateException;
/**
 * Data Services.
 *
 * @author Picado, Juan juan@encuestame.org
 * @since April 27, 2009
 */
public class DataService extends Service implements IDataService {

    private CatStateDaoImp stateDao;
    private CatLocationDao catLocationDao;
    private ProjectDaoImp projectDaoImp;

    /**
     * 
     * @return
     */
    public Collection<UnitProjectBean> loadListProjects() {
        Collection<UnitProjectBean> listProjects = new LinkedList<UnitProjectBean>();
        Collection<Project> list = getProjectDaoImp().findAll();
        log.info("list getProyectDaoImp->" + list.size());
        if (list != null && list.size() > 0) {
            for (Iterator<Project> i = list.iterator(); i.hasNext();) {
                UnitProjectBean proB = new UnitProjectBean();
                Project project = i.next();
                proB.setId(Integer.valueOf(project.getProyectId().toString()));
                proB.setName(project.getDescription());
                proB.setDescription(project.getInfo());
                proB.setDateInit(project.getDateStart());
                proB.setDateFinish(project.getDateFinish());
                // falta agregar lista de grupos, usuarios y grupos de encuestas
                listProjects.add(proB);
            }
        }
        log.info("list listProjects->" + listProjects.size());
        return listProjects;
    }

    /**
     * load project info
     *
     * @param id
     * @return
     * @throws EnMeExpcetion
     */
    public UnitProjectBean loadProjectInfo(UnitProjectBean project) throws EnMeExpcetion {
        log.info("loadProjectInfo DATASERVICE -->"+project);
        if (project.getId()!= null) {
            Project pro = getProjectDaoImp().getProjectbyId(project.getId());
            if (pro != null) {
                log.info("2 project found name ->"+pro.getDescription());
                project.setId(Integer.valueOf(pro.getProyectId().toString()));
                project.setDateFinish(pro.getDateFinish());
                project.setDateInit(pro.getDateStart());
                project.setDescription(pro.getInfo());
                project.setName(pro.getDescription());
                project.setState(Integer.valueOf(pro.getCatState().getIdState().toString()));
                log.info("Rescue->"+project.getName());
                log.info("loadProjectInfo DATASERVICE Rescue -->"+project);
                return project;
            } else {
                log.info("id project is not found");
                throw new EnMeExpcetion("id project is not found");
            }
        } else {
            log.info("id project is null");
            throw new EnMeExpcetion("id project is null");
        }

    }

    /**
     * create project
     *
     * @param project
     * @throws EnMeExpcetion
     */
    public void createProject(UnitProjectBean project) throws EnMeExpcetion,
            HibernateException, Exception {
        log.info("create project");
        if (project != null) {
            try {
                Project proB = new Project();
                proB.setCatState(getState(project.getState()));
                proB.setDateFinish(project.getDateFinish());
                proB.setDateStart(project.getDateInit());
                proB.setDescription(project.getName());
                proB.setInfo(project.getDescription());
                log.info("create project 2");
                getProjectDaoImp().saveOrUpdate(proB);
            } catch (HibernateException e) {
                throw new HibernateException(e);
            } catch (Exception e) {
                throw new Exception(e);
            }
        } else {
            throw new EnMeExpcetion("project is null");
        }
    }

    /**
     * load state by id
     *
     * @param id
     */
    private CatState getState(Integer id) throws Exception {
        return getStateDao().getState(id);
    }

    /**
     *
     * @return
     */
    public CatStateDaoImp getStateDao() {
        return stateDao;
    }

    /**
     *
     * @param stateDao
     */
    public void setStateDao(CatStateDaoImp stateDao) {
        this.stateDao = stateDao;
    }

    /**
     * @return the catLocationDao
     */
    public CatLocationDao getCatLocationDao() {
        return catLocationDao;
    }

    /**
     * @param catLocationDao the catLocationDao to set
     */
    public void setCatLocationDao(CatLocationDao catLocationDao) {
        this.catLocationDao = catLocationDao;
    }

    /**
     * @return the projectDaoImp
     */
    public ProjectDaoImp getProjectDaoImp() {
        return projectDaoImp;
    }

    /**
     * @param projectDaoImp the projectDaoImp to set
     */
    public void setProjectDaoImp(ProjectDaoImp projectDaoImp) {
        this.projectDaoImp = projectDaoImp;
    }
}
