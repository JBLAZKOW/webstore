package pl.spring.demo.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;

import pl.spring.demo.dao.BookDao;
import pl.spring.demo.entity.BookEntity;

public class BookDaoImpl extends AbstractDao<BookEntity, Long> implements BookDao {

    @Override
    public List<BookEntity> findBookByTitle(String title) {
        TypedQuery<BookEntity> query = entityManager.createQuery(
                "select book from BookEntity book where upper(book.title) like concat(upper(:title), '%')", BookEntity.class);
        query.setParameter("title", title);
        return query.getResultList();
    }
}
