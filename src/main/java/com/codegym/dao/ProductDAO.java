package com.codegym.dao;

import com.codegym.model.Product;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;


public class ProductDAO implements IProductDAO{
    private static SessionFactory sessionFactory;
    private static EntityManager entityManager;

    static {
        try {
            sessionFactory = new Configuration()
                    .configure("hibernate.conf.xml")
                    .buildSessionFactory();
            entityManager = sessionFactory.createEntityManager();
        } catch (HibernateException e) {
            e.printStackTrace();
        }
    }


    @Override
    public List<Product> findAll() {
        String query = "SELECT p FROM Product AS p";
        TypedQuery<Product> typedQuery = entityManager.createQuery(query, Product.class);
        return typedQuery.getResultList();
    }

    @Override
    public Product findById(Long id) {
        if (id == null) {
            return new Product();
        }
        String query = "SELECT p FROM Product AS p WHERE p.id = :id";
        TypedQuery<Product> typedQuery = entityManager.createQuery(query, Product.class);
        typedQuery.setParameter("id", id);
        return typedQuery.getSingleResult();
    }

    @Override
    public List<Product> findByName(String name) {
        String query = "SELECT p FROM Product AS p WHERE p.name like :name";
        TypedQuery<Product> typedQuery = entityManager.createQuery(query, Product.class);
        typedQuery.setParameter("name", name);
        return typedQuery.getResultList();
    }

    @Override
    public Product save(Product product) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            Product oldProduct = findById(product.getId()); //T??m xem c?? product truy???n v??o c?? id hay kh??ng
            oldProduct.setName(product.getName());
            oldProduct.setPrice(product.getPrice());
            oldProduct.setDescription(product.getDescription());
            oldProduct.setImage(product.getImage());
            session.saveOrUpdate(oldProduct);
            transaction.commit();
            return oldProduct;
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return null;
    }

    @Override
    public void removeById(Long id) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        Product product = findById(id);
        session.delete(product);
        transaction.commit();
    }


}
