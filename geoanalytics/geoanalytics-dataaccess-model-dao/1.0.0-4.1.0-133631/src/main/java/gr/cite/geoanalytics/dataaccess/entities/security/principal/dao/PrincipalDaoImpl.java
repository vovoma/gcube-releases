package gr.cite.geoanalytics.dataaccess.entities.security.principal.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import gr.cite.geoanalytics.dataaccess.dao.JpaDao;
import gr.cite.geoanalytics.dataaccess.dao.UUIDGenerator;
import gr.cite.geoanalytics.dataaccess.entities.ActiveStatus;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.principal.PrincipalClass;
import gr.cite.geoanalytics.dataaccess.entities.tenant.Tenant;

@Repository
public class PrincipalDaoImpl extends JpaDao<Principal, UUID> implements PrincipalDao {
	
	public static Logger log = LoggerFactory.getLogger(PrincipalDaoImpl.class);
	
	@Override
	public Principal loadDetails(Principal t) {
		if(t == null) return null;
		t.getMetadata();
		t.getProviderDefinition();
		return t;
	}
	
	@Override
	public List<Principal> findActivePrincipals() {
		List<Principal> result = null;
		
		TypedQuery<Principal> query = entityManager.createQuery("from Principal p where p.isActive = :active", Principal.class);
		query.setParameter("active", ActiveStatus.ACTIVE.code());
		result = query.getResultList();
		
		if(result == null) return new ArrayList<Principal>();
		return result;
	}
	
	@Override
	public List<Principal> findByClass(PrincipalClass classType) {
		return findByClass(classType, ActiveStatus.ACTIVE);
	}
	
//	String stringQuery = "SELECT NEW gr.cite.alumni.model.entities.user.dao.UserValidationIdentityName(uv, i.name, u.shortName, u.fullName) FROM UserValidation uv join uv.user u inner join u.identity i  " + 
//			"where (uv.istatus = :IStatus OR uv.istatus = :IStatus2 OR uv.istatus = :IStatus3 OR uv.istatus = :IStatus4) AND lower(uv.site) = lower(:Site) " +
//			 middleSentence +
//			" ORDER BY " + orderTerm + " " + ascdesc;//a.graduationYear";

	@Override
	public List<PrincipalProjectInfoDao> findByClassReturnsPrincipalProjectInfo(PrincipalClass  classType, ActiveStatus activeStatus){
		StringBuilder queryB = new StringBuilder("SELECT new gr.cite.geoanalytics.dataaccess.entities.security.principal.dao.PrincipalProjectInfoDao(p.name, p.principalData.email, count(pp)) FROM Principal p left join p.projectsParticipant pp where p.classId = :classId");
		if(activeStatus != null)
			queryB.append(" and p.isActive = :active group by p.name, p.principalData.email");
		
		TypedQuery<PrincipalProjectInfoDao> query = entityManager.createQuery(queryB.toString(), PrincipalProjectInfoDao.class);
		query.setParameter("classId", classType.classCode());
		if(activeStatus != null)
			query.setParameter("active", activeStatus.code());
		
		List<PrincipalProjectInfoDao> result =  query.getResultList();
		
		if(result == null) return new ArrayList<PrincipalProjectInfoDao>();
		return result;
	}
	
	@Override
	public List<Principal> findByClass(PrincipalClass classType, ActiveStatus activeStatus) {
		StringBuilder queryB = new StringBuilder("from Principal p where p.classId = :classId");
		if(activeStatus != null)
			queryB.append(" and p.isActive = :active");
		
		TypedQuery<Principal> query = entityManager.createQuery(queryB.toString(), Principal.class);
		query.setParameter("classId", classType.classCode());
		if(activeStatus != null)
			query.setParameter("active", activeStatus.code());
		
		List<Principal> result =  query.getResultList();
		
		if(result == null) return new ArrayList<Principal>();
		return result;
	}

	@Override
	public Principal findActivePrincipalByName(String name) {
		return findPrincipalByNameAndActivityStatus(name, ActiveStatus.ACTIVE);
	}
	
	@Override
	public Principal findPrincipalByNameAndActivityStatus(String name, ActiveStatus activeStatus) {

		StringBuilder queryB = new StringBuilder("from Principal p where p.name = :name");
		if(activeStatus != null){
			queryB.append(" and p.isActive = :active");
		}
		TypedQuery<Principal> query = entityManager.createQuery(queryB.toString(), Principal.class);
		
		query.setParameter("name", name);
		if(activeStatus != null){
			query.setParameter("active", activeStatus.code());
		}
		Principal result = null;
		try{
			result = query.getSingleResult();
		}catch (Exception e) {
			if(activeStatus != null)
				log.debug("Username: " + name + " does not exist.");
		}
		return result;
	}
	
	@Override
	public Principal systemPrincipal() {
		return read(UUIDGenerator.systemUserUUID());
	}

	@Override
	public Principal findPrincipalByNameTenantAndActiveStatus(String principalName, String tenantName, ActiveStatus activeStatus) {
		
		List<Principal> result = null;
		Principal principal = null;
		
		TypedQuery<Principal> query = entityManager.createQuery("from Principal p where p.isActive = :active and p.principal.name = :principalName and p.tenant.name = :tenantName", Principal.class);
		query.setParameter("active", activeStatus.code());
		query.setParameter("principalName", principalName);
		query.setParameter("tenantName", tenantName);
		
		result = query.getResultList();
		
		if (result.isEmpty()){
			log.debug("No results with the specified parameters");
			return principal;
		}else if (result.size() > 1){
			log.error("More than one result came back");
			throw new RuntimeException("More than one result came back");
		}
		principal = result.get(0);
		
		return principal;
	}
	
	@Override
	public Principal findPrincipalByNameAndTenant(String principalName, String tenantName) {
		
		List<Principal> result = null;
		Principal principal = null;
		
		TypedQuery<Principal> query = entityManager.createQuery("from Principal p where p.principal.name = :principalName and p.tenant.name = :tenantName", Principal.class);
		query.setParameter("principalName", principalName);
		query.setParameter("tenantName", tenantName);
		
		result = query.getResultList();
		
		if (result.isEmpty()){
			log.debug("No results with the specified parameters");
			return principal;
		}else if (result.size() > 1){
			log.error("More than one result came back");
			throw new RuntimeException("More than one result came back");
		}
		principal = result.get(0);
		
		return principal;
	}
	
	@Override
	public List<String> listActivePrincipalNames(){
		
		List<String> principalNames = null;
		
		TypedQuery<String> query = entityManager.createQuery("select p.name from Principal p where p.isActive = :activeStatus", String.class);
		query.setParameter("activeStatus", ActiveStatus.ACTIVE.code());
		principalNames = query.getResultList();
		return principalNames;
	}
	
	@Override
	public List<String> listPrincipalNames(){
		
		List<String> principalNames = null;
		TypedQuery<String> query = entityManager.createQuery("select p.name from Principal p", String.class);
		
		principalNames = query.getResultList();
		return principalNames;
	}
	
	@Override
	public List<String> listPrincipalNamesByTenant(Tenant tenant){
		
		List<String> principalNames = null;
		TypedQuery<String> query = entityManager.createQuery("select p.name from Principal p where p.tenant = :tenant", String.class);
		query.setParameter("tenant", tenant);
		
		principalNames = query.getResultList();
		return principalNames;
	}
	
	@Override
	public List<String> searchByPrincipalNamesAndTenant(List<String> principalNames, Tenant tenant) {
		List<String> result = null;
		
		StringBuilder queryB = new StringBuilder();
		queryB.append("select p.name from Principal p where p.tenant = :tenant");
		if(!principalNames.isEmpty()) queryB.append(" and ( ");
		int j = 0;
		for(int i=0; i<principalNames.size(); i++) {
			queryB.append("lower(p.name) = :principalnames" + j);
			j++;
			queryB.append(" or lower(p.principalData.fullName) like :principalnames" + j);
			j++;
			if(i < principalNames.size()-1)
				queryB.append(" or ");
		}
		if(!principalNames.isEmpty()) queryB.append(")");
		TypedQuery<String> query = entityManager.createQuery(queryB.toString(), String.class);
		
		j = 0;
		for(int i=0; i<principalNames.size(); i++) {
			String lower = principalNames.get(i).toLowerCase();
			query.setParameter("principalNames"+(j++), lower);
			query.setParameter("principalNames"+(j++), "%"+lower+"%");
		}
		query.setParameter("c", tenant);
		
		result = query.getResultList();
		
		log.debug("Principals by tenants and name pattern matching:");
		log.debug((result != null ? result.size() : 0) + " results");
		if(log.isDebugEnabled() && result != null) {
			for (String us : (List<String>) result) 
				log.debug("Principal (" + us + ")");
		}
		
		return result;
	}
	
	@Override
	public Principal findActivePrincipalByNameAndTenant(String principalName, String tenantName) {
		return this.findPrincipalByNameTenantAndActiveStatus(principalName, tenantName, ActiveStatus.ACTIVE);
	}
}
