package com.flipkart.restController;
import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.flipkart.bean.Course;
import com.flipkart.bean.Professor;
import com.flipkart.exception.AddCourseException;
import com.flipkart.exception.CourseNotDeletedException;
import com.flipkart.exception.CourseNotFoundException;
import com.flipkart.exception.ProfessorNotAddedException;
import com.flipkart.exception.ProfessorNotDeletedException;
import com.flipkart.exception.StudentNotFoundForVerificationException;
import com.flipkart.exception.StudentNotRegisteredException;
import com.flipkart.exception.UserAlreadyExistException;
import com.flipkart.service.AdminInterface;
import com.flipkart.service.AdminOperation;
@Path("/course")
public class CourseRestAPI {
    
    AdminInterface adminOperation = AdminOperation.getInstance();
    	/**
	 * /admin/addCourse
	 * REST-service for adding a new course in catalog
	 * @param course
	 * @return
	 */
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addCourse(@Valid Course course) throws ValidationException{

		try {
			adminOperation.addCourse(course);
			return Response.status(201).entity("Course with courseId: " + course.getCourseId() + " added to catalog").build();
		} catch (AddCourseException e) {
			return Response.status(409).entity(e.getMessage()).build();
		}
			
	}


	/**
	 * /admin/deleteCourse
	 * REST-services for dropping a course from catalog
	 * @param courseCode
	 * @return
	 */
	@DELETE
	@Path("/{courseId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteCourse(

			@NotNull
			@PathParam("courseId") String courseCode) throws ValidationException{
		
		try {
			adminOperation.removeCourse(courseCode);
			return Response.status(204).entity("Course with courseCode: " + courseCode + " deleted from catalog").build();
		} catch (CourseNotFoundException | CourseNotDeletedException e) {
			return Response.status(409).entity(e.getMessage()).build();
		}
		
	}
	
}
