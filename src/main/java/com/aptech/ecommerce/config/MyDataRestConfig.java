package com.aptech.ecommerce.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import com.aptech.ecommerce.entity.*;


@Configuration
public class MyDataRestConfig implements RepositoryRestConfigurer{
	
	private EntityManager entityManager;
	
	@Autowired
	public MyDataRestConfig(EntityManager entityManager) {
		this.entityManager = entityManager;
	}	

	@Override
	public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
			
		HttpMethod[] theUnsupportedAction = {HttpMethod.PUT, HttpMethod.POST, HttpMethod.DELETE};
		
//		disableHttpMethod(Product.class, config, theUnsupportedAction);
		disableHttpMethod(Category.class, config, theUnsupportedAction);
		
		config.exposeIdsFor(arrayOfEntities());
	}

	private void disableHttpMethod(Class theClass, RepositoryRestConfiguration config, HttpMethod[] theUnsupportedAction) {
		config.getExposureConfiguration()
				.forDomainType(theClass)
				.withItemExposure((metdata, httpMethods) -> httpMethods.disable(theUnsupportedAction))
				.withCollectionExposure((metdata, httpMethods) -> httpMethods.disable(theUnsupportedAction));
	}
	
	private Class<?>[] arrayOfEntities() {
		// get list of all entity classes true metadata
		Set<EntityType<?>> entity = entityManager.getMetamodel().getEntities();
		
		// create an array of entity
		List<Class> entityClasses = new ArrayList<>();
		
		// get entity type
		for (EntityType tempEntityType: entity) {
			entityClasses.add(tempEntityType.getJavaType());
		}
		
		return entityClasses.toArray(new Class[0]);
	}
}
