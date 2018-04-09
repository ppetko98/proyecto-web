/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mvc.modelo.dao.impl;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import mvc.modelo.entidades.Imagenes;
import mvc.modelo.entidades.Nomenclatura;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.UserTransaction;
import mvc.modelo.dao.impl.exceptions.IllegalOrphanException;
import mvc.modelo.dao.impl.exceptions.NonexistentEntityException;
import mvc.modelo.dao.impl.exceptions.RollbackFailureException;
import mvc.modelo.entidades.Filo;

/**
 *
 * @author Tamara
 */
public class FiloControllerImpl implements Serializable {

    public FiloControllerImpl() {
        this.utx = utx;
        this.emf = emf = Persistence.createEntityManagerFactory("PU");;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Filo filo) throws RollbackFailureException, Exception {
        if (filo.getNomenclaturaCollection() == null) {
            filo.setNomenclaturaCollection(new ArrayList<Nomenclatura>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Imagenes imagen = filo.getImagen();
            if (imagen != null) {
                imagen = em.getReference(imagen.getClass(), imagen.getIdImagenes());
                filo.setImagen(imagen);
            }
            Collection<Nomenclatura> attachedNomenclaturaCollection = new ArrayList<Nomenclatura>();
            for (Nomenclatura nomenclaturaCollectionNomenclaturaToAttach : filo.getNomenclaturaCollection()) {
                nomenclaturaCollectionNomenclaturaToAttach = em.getReference(nomenclaturaCollectionNomenclaturaToAttach.getClass(), nomenclaturaCollectionNomenclaturaToAttach.getNomenclaturaPK());
                attachedNomenclaturaCollection.add(nomenclaturaCollectionNomenclaturaToAttach);
            }
            filo.setNomenclaturaCollection(attachedNomenclaturaCollection);
            em.persist(filo);
            if (imagen != null) {
                imagen.getFiloCollection().add(filo);
                imagen = em.merge(imagen);
            }
            for (Nomenclatura nomenclaturaCollectionNomenclatura : filo.getNomenclaturaCollection()) {
                Filo oldFiloOfNomenclaturaCollectionNomenclatura = nomenclaturaCollectionNomenclatura.getFilo();
                nomenclaturaCollectionNomenclatura.setFilo(filo);
                nomenclaturaCollectionNomenclatura = em.merge(nomenclaturaCollectionNomenclatura);
                if (oldFiloOfNomenclaturaCollectionNomenclatura != null) {
                    oldFiloOfNomenclaturaCollectionNomenclatura.getNomenclaturaCollection().remove(nomenclaturaCollectionNomenclatura);
                    oldFiloOfNomenclaturaCollectionNomenclatura = em.merge(oldFiloOfNomenclaturaCollectionNomenclatura);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Filo filo) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Filo persistentFilo = em.find(Filo.class, filo.getIdFilo());
            Imagenes imagenOld = persistentFilo.getImagen();
            Imagenes imagenNew = filo.getImagen();
            Collection<Nomenclatura> nomenclaturaCollectionOld = persistentFilo.getNomenclaturaCollection();
            Collection<Nomenclatura> nomenclaturaCollectionNew = filo.getNomenclaturaCollection();
            List<String> illegalOrphanMessages = null;
            for (Nomenclatura nomenclaturaCollectionOldNomenclatura : nomenclaturaCollectionOld) {
                if (!nomenclaturaCollectionNew.contains(nomenclaturaCollectionOldNomenclatura)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Nomenclatura " + nomenclaturaCollectionOldNomenclatura + " since its filo field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (imagenNew != null) {
                imagenNew = em.getReference(imagenNew.getClass(), imagenNew.getIdImagenes());
                filo.setImagen(imagenNew);
            }
            Collection<Nomenclatura> attachedNomenclaturaCollectionNew = new ArrayList<Nomenclatura>();
            for (Nomenclatura nomenclaturaCollectionNewNomenclaturaToAttach : nomenclaturaCollectionNew) {
                nomenclaturaCollectionNewNomenclaturaToAttach = em.getReference(nomenclaturaCollectionNewNomenclaturaToAttach.getClass(), nomenclaturaCollectionNewNomenclaturaToAttach.getNomenclaturaPK());
                attachedNomenclaturaCollectionNew.add(nomenclaturaCollectionNewNomenclaturaToAttach);
            }
            nomenclaturaCollectionNew = attachedNomenclaturaCollectionNew;
            filo.setNomenclaturaCollection(nomenclaturaCollectionNew);
            filo = em.merge(filo);
            if (imagenOld != null && !imagenOld.equals(imagenNew)) {
                imagenOld.getFiloCollection().remove(filo);
                imagenOld = em.merge(imagenOld);
            }
            if (imagenNew != null && !imagenNew.equals(imagenOld)) {
                imagenNew.getFiloCollection().add(filo);
                imagenNew = em.merge(imagenNew);
            }
            for (Nomenclatura nomenclaturaCollectionNewNomenclatura : nomenclaturaCollectionNew) {
                if (!nomenclaturaCollectionOld.contains(nomenclaturaCollectionNewNomenclatura)) {
                    Filo oldFiloOfNomenclaturaCollectionNewNomenclatura = nomenclaturaCollectionNewNomenclatura.getFilo();
                    nomenclaturaCollectionNewNomenclatura.setFilo(filo);
                    nomenclaturaCollectionNewNomenclatura = em.merge(nomenclaturaCollectionNewNomenclatura);
                    if (oldFiloOfNomenclaturaCollectionNewNomenclatura != null && !oldFiloOfNomenclaturaCollectionNewNomenclatura.equals(filo)) {
                        oldFiloOfNomenclaturaCollectionNewNomenclatura.getNomenclaturaCollection().remove(nomenclaturaCollectionNewNomenclatura);
                        oldFiloOfNomenclaturaCollectionNewNomenclatura = em.merge(oldFiloOfNomenclaturaCollectionNewNomenclatura);
                    }
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = filo.getIdFilo();
                if (findFilo(id) == null) {
                    throw new NonexistentEntityException("The filo with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Filo filo;
            try {
                filo = em.getReference(Filo.class, id);
                filo.getIdFilo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The filo with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Nomenclatura> nomenclaturaCollectionOrphanCheck = filo.getNomenclaturaCollection();
            for (Nomenclatura nomenclaturaCollectionOrphanCheckNomenclatura : nomenclaturaCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Filo (" + filo + ") cannot be destroyed since the Nomenclatura " + nomenclaturaCollectionOrphanCheckNomenclatura + " in its nomenclaturaCollection field has a non-nullable filo field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Imagenes imagen = filo.getImagen();
            if (imagen != null) {
                imagen.getFiloCollection().remove(filo);
                imagen = em.merge(imagen);
            }
            em.remove(filo);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Filo> findFiloEntities() {
        return findFiloEntities(true, -1, -1);
    }

    public List<Filo> findFiloEntities(int maxResults, int firstResult) {
        return findFiloEntities(false, maxResults, firstResult);
    }

    private List<Filo> findFiloEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Filo.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Filo findFilo(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Filo.class, id);
        } finally {
            em.close();
        }
    }

    public int getFiloCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Filo> rt = cq.from(Filo.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
