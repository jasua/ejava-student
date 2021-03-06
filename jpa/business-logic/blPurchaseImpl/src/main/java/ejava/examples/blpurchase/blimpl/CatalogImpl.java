package ejava.examples.blpurchase.blimpl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.blpurchase.bl.Catalog;
import ejava.examples.blpurchase.bo.Account;
import ejava.examples.blpurchase.bo.Cart;
import ejava.examples.blpurchase.bo.Product;

public class CatalogImpl implements Catalog {
	private static final Log log = LogFactory.getLog(CatalogImpl.class);
	private EntityManager em;

	public void setEntityManager(EntityManager entityManager) {
		em = entityManager;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Product> getProducts(int offset, int limit) {
		Query query = em.createQuery("select p from Product p");
		if (offset > 0) {
			query.setFirstResult(offset);
		}
		if (limit > 0) {
			query.setMaxResults(limit);
		}
		return query.getResultList();
	}

	@Override
	public int addToCart(int id, String email) {
		Product product = em.find(Product.class, id);
		if (product == null) {
			log.warn("product not found:" + id);
			return 0;
		}
		if (product.getCount()-1 < 0) {
			log.warn("no product left");
			return 0;
		}
		product.setCount(product.getCount()-1);

		Cart cart = em.find(Cart.class, email);
		if (cart == null) {
			List<Account> accounts = em.createNamedQuery(
					Account.FIND_BY_EMAIL, Account.class)
					.setParameter("email", email)
					.getResultList();
			if (accounts.size() == 0) {
				log.warn("no account found for:" + email);
				return 0;
			}
			cart = new Cart(accounts.get(0));
			em.persist(cart);
		}
		cart.getProducts().add(product);
		
		return cart.getProducts().size();
	}
}
