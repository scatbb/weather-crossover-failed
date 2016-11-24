package com.crossover.trial.weather.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.crossover.trial.weather.entities.Airport;

/**
 * Data access object for Airports
 * 
 * CRUD routines
 * 
 * @author LEIG
 *
 */
public class WeatherAirportsDAO 
{
	private EntityManager em;
	
	public WeatherAirportsDAO(EntityManagerFactory emf)
	{
		this.em = emf.createEntityManager();
	}
	
	/**
	 * Get airport
	 * 
	 * @param id
	 * @return
	 */
	public Airport getAirport(Long id) throws Exception
	{
		Airport a = em.find(Airport.class, id);
		if(null == a)
		{
			throw new Exception("Airport not found");
		}
		return a;
	}
	
	/**
	 * Get airport list
	 * 
	 * @return
	 */
	public List<Airport> getAirports()
	{
		List<Airport> aList = em.createQuery(
    			"SELECT a FROM Airport a")
    			.getResultList();
		
		return aList;
	}
	
	public Airport getAirportByIATA(String iata) throws Exception
	{
		List<Airport> aList = em.createQuery(
    			"SELECT a FROM Airport a WHERE iata = :iata")
				.setParameter("iata", iata)
    			.getResultList();
		if(0 == aList.size())
		{
			throw new Exception("Airport not found");
		}
		return aList.get(0);
	}
	
	/**
	 * Create airport
	 * 
	 * @param a
	 */
	public void createAirport(Airport a) throws Exception
	{
		try
		{
			em.getTransaction().begin();
			em.persist(a);
			em.flush();
			em.getTransaction().commit();
		}
		catch(Exception ex)
		{
			em.getTransaction().rollback();
			throw new Exception("Airport could not be created: " + ex.getMessage());
		}
	}
	
	/**
	 * Update airport
	 * 
	 * @param a
	 */
	public void updateAirport(Airport a) throws Exception
	{
		try
		{
			em.getTransaction().begin();
			em.merge(a);
			em.flush();
			em.getTransaction().commit();
		}
		catch(Exception ex)
		{
			em.getTransaction().rollback();
			throw new Exception("Airport could not be created: " + ex.getMessage());
		}			
	}
	
	/**
	 * Delete airport
	 * 
	 * @param a
	 */
	public void deleteAirport(Airport a) throws Exception
	{
		try
		{
			em.getTransaction().begin();
			em.remove(a);
			em.flush();
			em.getTransaction().commit();
		}
		catch(Exception ex)
		{
			em.getTransaction().rollback();
			throw new Exception("Airport could not be deleted: " + ex.getMessage());
		}
	}
	/**
	 * Delete all airports
	 * 
	 * @param a
	 */
	public void deleteAllAirports() throws Exception
	{
		try
		{
			em.getTransaction().begin();
			em.createQuery("DELETE FROM Airport").executeUpdate();			
			em.flush();
			em.getTransaction().commit();
		}
		catch(Exception ex)
		{
			em.getTransaction().rollback();
			throw new Exception("Airports could not be deleted: " + ex.getMessage());
		}
	}
	
	/**
	 * Delete airport by iata
	 * 
	 * @param a
	 */
	public void deleteAirportByIATA(String iata) throws Exception
	{
		Airport a = getAirportByIATA(iata);
		if(a != null)
		{
			try
			{
				em.getTransaction().begin();
				em.remove(a);
				em.flush();
				em.getTransaction().commit();
			}
			catch(Exception ex)
			{
				em.getTransaction().rollback();
				throw new Exception("Airport could not be deleted: " + ex.getMessage());
			}
		}
	}
}
