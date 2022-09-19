package com.flipkart.restController;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.flipkart.service.AdminInterface;
import com.flipkart.service.AdminOperation;

@Path("/endregistration")
public class EndRegistrationRestAPI {
    
    AdminInterface adminOperation = AdminOperation.getInstance();

	/**
	 * /admin/deleteCourse
	 * REST-services for dropping a course from catalog
	 * @param courseCode
	 * @return
	 */


	@PUT
	@Path("/")
	public Response end_Registration(
		@QueryParam("semester") int semester){
		int res=adminOperation.endRegistration(semester);
		return Response.status(200).entity("Completed registration process and number of courses cancelled is:  " + res).build();
	}

	
	
}
