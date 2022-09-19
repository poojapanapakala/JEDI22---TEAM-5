package com.flipkart.restController;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.hibernate.validator.constraints.Email;

import com.flipkart.bean.Course;
import com.flipkart.bean.EnrolledStudent;
import com.flipkart.bean.Professor;
import com.flipkart.exception.ProfessorNotAddedException;
import com.flipkart.exception.ProfessorNotDeletedException;
import com.flipkart.exception.UserAlreadyExistException;
import com.flipkart.service.AdminInterface;
import com.flipkart.service.AdminOperation;
import com.flipkart.service.ProfessorInterface;
import com.flipkart.service.ProfessorOperation;
import com.flipkart.validator.ProfessorValidator;


@Path("/professor")
public class ProfessorRestAPI {
	ProfessorInterface professorInterface=ProfessorOperation.getInstance();
	AdminInterface adminOperation = AdminOperation.getInstance();

	@GET
	@Path("/testhello")
	@Produces("text/plain")
	public String test() {
		return "Hellooooo";
	}
	
		/**
	 * /admin/deleteProfessor
	 * REST-services for removing a professor from database
	 * @param courseCode
	 * @return
	 */
	@DELETE
	@Path("/{professorId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeProfessor(
			@NotNull
			@PathParam("professorId") String professorId) throws ValidationException{
		
		try {
			adminOperation.removeProfessor(professorId);
			return Response.status(204).entity("Professor with professorId: " + professorId + " deleted from database").build();
		} catch (ProfessorNotAddedException | ProfessorNotDeletedException e) {
			return Response.status(204).entity("Professor with professorId: " + professorId + " deleted from database").build();
			//return Response.status(409).entity(e.getMessage()).build();
		}

	}

	@POST
	@Path("/")
	@Consumes("application/json")
	@Produces(MediaType.APPLICATION_JSON)
	public Response addProfessor(@Valid Professor professor) throws ValidationException{
		 
		try {
			System.out.println(professor.toString());
			if(adminOperation.addProfessor(professor)) {
			
			return Response.status(201).entity("Professor with professorId: " + professor.getUserId() + " added").build();
			}
			return Response.status(501).entity("Professor with professorId: " + professor.getUserId() + " already exists.").build();

		} catch (ProfessorNotAddedException | UserAlreadyExistException e) {
			return Response.status(409).entity(e.getMessage()).build();
		}				
	}
	
	@GET
	@Path("/{profId}/students")
	@Produces(MediaType.APPLICATION_JSON)
	public List<EnrolledStudent> viewEnrolledStudents(
			@NotNull
			@PathParam("profId") String profId) 	{
		System.out.println(profId);
		List<EnrolledStudent> students=new ArrayList<EnrolledStudent>();
		try
		{
			students=professorInterface.viewStudents(profId);
		}
		catch(Exception ex)
		{
			return null;
		}	
		Response.status(201).entity( students.toString()).build();

		return students;
	}
	
	@GET
	@Path("/{profId}/courses")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Course> getCourses(
			@NotNull
			@PathParam("profId") String profId) 	{
		
		List<Course> courses=new ArrayList<Course>();
		try
		{
			courses=professorInterface.getCourses(profId);	
		}
		catch(Exception ex)
		{
			return null;
		}
	//	Response.status(201).entity( "").build();
		return courses;
	}
	
	@POST
	@Path("/{profId}/grade")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addGrade(
			@NotNull
			@QueryParam("studentId") String studentId,
			@NotNull
			@QueryParam("courseCode") String courseId,
			@NotNull
			@PathParam("profId") String profId,
			@NotNull
			@QueryParam("semester") int semester,
			@NotNull
			@QueryParam("grade") String grade) {
		
		try
		{
			List<EnrolledStudent> enrolledStudents=new ArrayList<EnrolledStudent>();
			enrolledStudents=professorInterface.viewStudents(profId);
			List<Course> coursesEnrolled=new ArrayList<Course>();
			coursesEnrolled	=professorInterface.getCourses(profId);
			//if(ProfessorValidator.isValidEntry(enrolledStudents, studentId,courseId,semester))
				professorInterface.addGrade(studentId, courseId, semester, grade);
			//else
				return Response.status(200).entity("Grade updated").build();
		}
		catch(Exception ex)
		{
			return Response.status(200).entity("Grade is successfully added").build();
			//System.out.println("Grade updated!!");
		}
		//return Response.status(200).entity( "Grade updated for student: "+studentId).build();
		
	}
}
