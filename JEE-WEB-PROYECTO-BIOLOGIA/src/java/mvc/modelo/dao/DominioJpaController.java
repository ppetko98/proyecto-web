/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mvc.controlador;

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
import javax.transaction.UserTransaction;
import mvc.controlador.exceptions.IllegalOrphanException;
import mvc.controlador.exceptions.NonexistentEntityException;
import mvc.controlador.exceptions.RollbackFailureException;
import mvc.modelo.entidades.Dominio;

/**
 *
 * @author Tamara
 */
public class DominioJpaController implements Serializable {

    public DominioJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Dominio dominio) throws RollbackFailureException, Exception {
        if (dominio.getNomenclaturaCollection() == null) {
            dominio.setNomenclaturaCollection(new ArrayList<Nomenclatura>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Imagenes imagen = dominio.getImagen();
            if (imagen != null) {
                imagen = em.getReference(imagen.getClass(), imagen.getIdImagenes());
                dominio.setImagen(imagen);
            }
            Collection<Nomenclatura> attachedNomenclaturaCollection = new ArrayList<Nomenclatura>();
            for (Nomenclatura nomenclaturaCollectionNomenclaturaToAttach : dominio.getNomenclaturaCollection()) {
                nomenclaturaCollectionNomenclaturaToAttach = em.getReference(nomenclaturaCollectionNomenclaturaToAttach.getClass(), nomenclaturaCollectionNomenclaturaToAttach.getNomenclaturaPK());
                attachedNomenclaturaCollection.add(nomenclaturaCollectionNomenclaturaToAttach);
            }
            dominio.setNomenclaturaCollection(attachedNomenclaturaCollection);
            em.persist(dominio);
            if (imagen != null) {
                imagen.getDominioCollection().add(dominio);
                imagen = em.merge(imagen);
            }
            for (Nomenclatura nomenclaturaCollectionNomenclatura : dominio.getNomenclaturaCollection()) {
                Dominio oldDominioOfNomenclaturaCollectionNomenclatura = nomenclaturaCollectionNomenclatura.getDominio();
                nomenclaturaCollectionNomenclatura.setDominio(dominio);
                nomenclaturaCollectionNomenclatura = em.merge(nomenclaturaCollectionNomenclatura);
                if (oldDominioOfNomenclaturaCollectionNomenclatura != null) {
                    oldDominioOfNomenclaturaCollectionNomenclatura.getNomenclaturaCollection().remove(nomenclaturaCollectionNomenclatura);
                    oldDominioOfNomenclaturaCollectionNomenclatura = em.merge(oldDominioOfNomenclaturaCollectionNomenclatura);
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

    public void edit(Dominio dominio) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Dominio persistentDominio = em.find(Dominio.class, dominio.getIdDominio());
            Imagenes imagenOld = persistentDominio.getImagen();
            Imagenes imagenNew = dominio.getImagen();
            Collection<Nomenclatura> nomenclaturaCollectionOld = persistentDominio.getNomenclaturaCollection();
            Collection<Nomenclatura> nomenclaturaCollectionNew = dominio.getNomenclaturaCollection();
            List<String> illegalOrphanMessages = null;
            for (Nomenclatura nomenclaturaCollectionOldNomenclatura : nomenclaturaCollectionOld) {
                if (!nomenclaturaCollectionNew.contains(nomenclaturaCollectionOldNomenclatura)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Nomenclatura " + nomenclaturaCollectionOldNomenclatura + " since its dominio field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (imagenNew != null) {
                imagenNew = em.getReference(imagenNew.getClass(), imagenNew.getIdImagenes());
                dominio.setImagen(imagenNew);
            }
            Collection<Nomenclatura> attachedNomenclaturaCollectionNew = new ArrayList<Nomenclatura>();
            for (Nomenclatura nomenclaturaCollectionNewNomenclaturaToAttach : nomenclaturaCollectionNew) {
                nomenclaturaCollectionNewNomenclaturaToAttach = em.getReference(nomenclaturaCollectionNewNomenclaturaToAttach.getClass(), nomenclaturaCollectionNewNomenclaturaToAttach.getNomenclaturaPK());
                attachedNomenclaturaCollectionNew.add(nomenclaturaCollectionNewNomenclaturaToAttach);
            }
            nomenclaturaCollectionNew = attachedNomenclaturaCollectionNew;
            dominio.setNomenclaturaCollection(nomenclaturaCollectionNew);
            dominio = em.merge(dominio);
            if (imagenOld != null && !imagenOld.equals(imagenNew)) {
                imagenOld.getDominioCollection().remove(dominio);
                imagenOld = em.merge(imagenOld);
            }
            if (imagenNew != null && !imagenNew.equals(imagenOld)) {
                imagenNew.getDominioCollection().add(dominio);
                imagenNew = em.merge(imagenNew);
            }
            for (Nomenclatura nomenclaturaCollectionNewNomenclatura : nomenclaturaCollectionNew) {
                if (!nomenclaturaCollectionOld.contains(nomenclaturaCollectionNewNomenclatura)) {
                    Dominio oldDominioOfNomenclaturaCollectionNewNomenclatura = nomenclaturaCollectionNewNomenclatura.getDominio();
                    nomenclaturaCollectionNewNomenclatura.setDominio(dominio);
                    nomenclaturaCollectionNewNomenclatura = em.merge(nomenclaturaCollectionNewNomenclatura);
                    if (oldDominioOfNomenclaturaCollectionNewNomenclatura != null && !oldDominioOfNomenclaturaCollectionNewNomenclatura.equals(dominio)) {
                        oldDominioOfNomenclaturaCollectionNewNomenclatura.getNomenclaturaCollection().remove(nomenclaturaCollectionNewNomenclatura);
                        oldDominioOfNomenclaturaCollectionNewNomenclatura = em.merge(oldDominioOfNomenclaturaCollectionNewNomenclatura);
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
                Integer id = dominio.getIdDominio();
                if (findDominio(id) == null) {
                    throw new NonexistentEntityException("The dominio with id " + id + " no longer exists.");
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
            Dominio dominio;
            try {
                dominio = em.getReference(Dominio.class, id);
                dominio.getIdDominio();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The dominio with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Nomenclatura> nomenclaturaCollectionOrphanCheck = dominio.getNomenclaturaCollection();
            for (Nomenclatura nomenclaturaCollectionOrphanCheckNomenclatura : nomenclaturaCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Dominio (" + dominio + ") cannot be destroyed since the Nomenclatura " + nomenclaturaCollectionOrphanCheckNomenclatura + " in its nomenclaturaCollection field has a non-nullable dominio field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Imagenes imagen = dominio.getImagen();
            if (imagen != null) {
                imagen.getDominioCollection().remove(dominio);
                imagen = em.merge(imagen);
            }
            em.remove(dominio);
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

    public List<Dominio> findDominioEntities() {
        return findDominioEntities(true, -1, -1);
    }

    public List<Dominio> findDominioEntities(int maxResults, int firstResult) {
        return findDominioEntities(false, maxResults, firstResult);
    }

    private List<Dominio> findDominioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Dominio.class));
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

    public Dominio findDominio(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Dominio.class, id);
        } finally {
            em.close();
        }
    }

    public int getDominioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Dominio> rt = cq.from(Dominio.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
