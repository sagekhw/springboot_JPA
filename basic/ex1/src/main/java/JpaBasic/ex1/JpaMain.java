package JpaBasic.ex1;


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {

	// persistence.xml -> "<persistence-unit name="hello">"
	public static String PERSISTENCE_UNIT_NAME = "hello";

	public static void main(String[] args) {
		//JPA
		save(1L,"helloA");
		save(2L,"helloB");
//		update(2L,"updateB");
//		delete(1L);

		save(3L,"helloC");
		save(4L,"helloD");

		// JPQL findAll query
		findAll();
	}

	public static void save(Long id, String name){
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		EntityManager entityManager = entityManagerFactory.createEntityManager();

		EntityTransaction entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();
		try{
			// 비영속 상태
			Member member = new Member();
			member.setId(id);
			member.setName(name);

			// 영속 상태
			entityManager.persist(member); // save persistence context

			// 영속 컨텍스트에서 분리, 준영속 상태
			entityManager.detach(member); // just like class

			// 객체를 삭제한 상태(삭제)
			entityManager.remove(member); // save and remove


			entityTransaction.commit();
		}catch (Exception e){
			entityTransaction.rollback();
		}finally {
			entityManager.close();
		}
		entityManagerFactory.close();
	}

	public static void update(Long id, String name){
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		EntityManager entityManager = entityManagerFactory.createEntityManager();

		EntityTransaction entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();
		try{
			Member member = entityManager.find(Member.class,id);
			System.out.println("========== point A ==========");
			member.setName(name);
			System.out.println("========== point B ==========");
			/*
			명시적으로 수정을 위한 sql을 실행 하여도 커밋 전에 한번만 update 처리 된다.
			*/
//			entityManager.persist(member);
			System.out.println("========== point C ==========");
			/*
				commit 전 entityManager로 가져온 member 데이터에 대해서
				변경된 정보가 있다면 update 쿼리 생성 후 처리
			 */
			entityTransaction.commit();
			System.out.println("========== point D ==========");
		}catch (Exception e){
			entityTransaction.rollback();
		}finally {
			entityManager.close();
		}
		entityManagerFactory.close();
	}

	public static void delete(Long id){
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		EntityManager entityManager = entityManagerFactory.createEntityManager();

		EntityTransaction entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();
		try{
			Member member = entityManager.find(Member.class,id);
			entityManager.remove(member);

			entityTransaction.commit();
		}catch (Exception e){
			entityTransaction.rollback();
		}finally {
			entityManager.close();
		}
		entityManagerFactory.close();
	}

	/* JPQL */
	public static void findAll(){
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		EntityManager entityManager = entityManagerFactory.createEntityManager();

		EntityTransaction entityTransaction = entityManager.getTransaction();
		entityTransaction.begin();
		try{
			List<Member> result = entityManager
					.createQuery("SELECT m FROM Member as m", Member.class)
//					.setFirstResult(5) /*페이징 관련 시작*/
//					.setMaxResults(8) /*페이징 관련 끝*/
					.getResultList();

			for(Member member:result){
				System.out.println("member name : "+member.getName());
			}
			entityTransaction.commit();
		}catch (Exception e){
			entityTransaction.rollback();
		}finally {
			entityManager.close();
		}
		entityManagerFactory.close();
	}


	/*
		추가 설명 :
			entityManager.persist(member); 는 DB에 수정 및 저장 하는 것이 아니라
			member(entity)를 영속성 컨텍스트 라는 곳에 저장 한다.
			- 영속성 컨텍스트(PersistenceContext)는 논리적인 개념.
			- 눈에 보이지 않는다.
			- Entity Manager를 통해서 영속성 컨텍스트에 접근.

	 */
}
