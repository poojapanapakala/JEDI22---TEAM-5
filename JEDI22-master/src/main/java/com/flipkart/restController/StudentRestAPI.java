/**
 * 
 */
package com.flipkart.restController;

/**
 * @author lenovo
 *
 */

import javax.validation.ValidationException;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import java.sql.SQLException;
import java.util.*;

import com.flipkart.service.AdminInterface;
import com.flipkart.service.AdminOperation;
import com.flipkart.service.ProfessorInterface;
import com.flipkart.service.ProfessorOperation;
import org.apache.log4j.Logger;
import com.flipkart.bean.Course;
import com.flipkart.bean.Payment;
import com.flipkart.bean.ReportCard;
import com.flipkart.dao.RegistrationDaoOperation;
import com.flipkart.exception.AddCourseException;
import com.flipkart.exception.CourseAlreadyRegisteredException;
import com.flipkart.exception.CourseLimitReachedException;
import com.flipkart.exception.CourseNotDeletedException;
import com.flipkart.exception.CourseNotFoundException;
import com.flipkart.exception.PaymentNotFoundException;
import com.flipkart.exception.SeatNotAvailableException;
import com.flipkart.exception.StudentNotFoundForVerificationException;
import com.flipkart.exception.StudentNotRegisteredException;
import com.flipkart.service.RegistrationInterface;
import com.flipkart.service.RegistrationOperation;


/**
 * @author JEDI - 03
 *
 */

@Path("/student")
public class StudentRestAPI {
	
	RegistrationInterface registrationInterface = RegistrationOperation.getInstance();
	ProfessorInterface professorInterface = ProfessorOperation.getInstance();
	AdminInterface adminOperation = AdminOperation.getInstance();

	private static Logger logger = Logger.getLogger(StudentRestAPI.class);

		/**
	 * /admin/genReport
	 * REST- for generating report card
	 * @param studentId 
	 * @param student Semester
	 * @return
	 */


	/**
	 * /admin/approveStudent
	 * REST-service for approving the student admission
	 * @param studentId
	 * @return
	 */
	@PUT
	@Path("/{studentId}/approve")
	@Produces(MediaType.APPLICATION_JSON)
	public Response approveStudent(
			@NotNull
			@PathParam("studentId") String studentId) throws ValidationException{
		try {
			adminOperation.approveStudent(studentId);
			return Response.status(204).entity("Student with studentId: " + studentId + " approved").build();
	
		} catch (StudentNotFoundForVerificationException e) {
			return Response.status(409).entity(e.getMessage()).build();
		}		
	}
	
/**
	 * Handles api request to add a course
	 * @param courseCode
	 * @param studentId
	 * @return
	 * @throws ValidationException
	 */
	@PUT
	@Path("/{studentId}/course/{courseCode}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response addCourse(
			@NotNull
			@PathParam("courseCode") String courseId,
			@NotNull
			@PathParam("studentId") String studentId,
			@NotNull
			@Min(value = 1, message = "Student ID should not be less than 1")
			@Max(value = 20, message = "Student ID should be less than 20")
			@QueryParam("semester") int semester) throws ValidationException{
	
		try{
			
			    List<Course> availCourseList = registrationInterface.viewCourses(studentId,semester);
			    boolean availabile = registrationInterface.checkCourse(courseId,studentId,semester);
				System.out.println(availabile);
				if(availabile){
					if(registrationInterface.addCourse(courseId, studentId,semester)) {
						return Response.status(201).entity( "You have successfully added Course : " + courseId).build();
					}
					return Response.status(501).entity( "Course with Course ID : " + courseId+" already exists.").build();

				}
				else {
					return Response.status(201).entity( "SEATS ARE FULL!!").build();
				}

			
			
		}
		catch (AddCourseException | CourseNotFoundException | CourseLimitReachedException | SQLException e) {
			System.out.println(e.getMessage());
			return Response.status(500).entity(e.getMessage()).build();

		}

	}

	
	/**
	 * /admin/reportGenerate
	 * REST-service for generating report
	 */
	@POST
	@Path("/{studentId}/report")
	@Consumes("application/json")
	@Produces(MediaType.APPLICATION_JSON)
	public Response generateReport(
			@NotNull
			@PathParam("studentId") String studentId,
			
			@NotNull
			@QueryParam("semester") int studentSem) throws ValidationException{
		
		try {
			adminOperation.generateReport(studentId,studentSem);
			return Response.status(201).entity("Generated Report Card for studentId: " + studentId ).build();
		} catch (StudentNotRegisteredException e) {
			return Response.status(409).entity(e.getMessage()).build();
		}
						
	}
	/**
	 * Handles API request to drop a course
	 * @param courseCode
	 * @param studentId
	 * @return
	 * @throws ValidationException
	 */
	@DELETE
	@Path("/{studentId}/course/{courseCode}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response dropCourse(
			@NotNull
			@PathParam("courseCode") String courseId,
			@NotNull
			@PathParam("studentId") String studentId,
			@NotNull
			@Min(value = 1, message = "Student ID should not be less than 1")
			@Max(value = 20, message = "Student ID should be less than 20")
			@QueryParam("semester") int semester) throws ValidationException{
		
		try{
			
			List<Course>registeredCourseList = registrationInterface.viewRegisteredCourses(studentId,semester);
			registrationInterface.dropCourse(courseId, studentId, semester);
			return Response.status(201).entity( "You have successfully dropped Course : " + courseId).build();
		}
		catch(CourseNotFoundException | SQLException | CourseNotDeletedException e)
		{	
			logger.info(e.getMessage());
			return Response.status(501).entity(e.getMessage()).build();
		} 
		
	}
	
	
	/**
	 * Method handles API request to view the list of available courses or registered courses for a student
	 * @param studentId
	 * @return
	 * @throws ValidationException
	 */
	@GET
	@Path("/{studentId}/course")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Course> viewCourse(
			@NotNull
			@PathParam("studentId") String studentId,
			@QueryParam("viewRegistered") boolean viewRegistered,
			@NotNull
			@Min(value = 1, message = "Student ID should not be less than 1")
			@Max(value = 20, message = "Student ID should be less than 20")
			@QueryParam("semester") int semester) throws ValidationException{
				try {
					if(viewRegistered) {
						return registrationInterface.viewRegisteredCourses(studentId,semester);
					} else {
						return registrationInterface.viewCourses(studentId,semester);
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
	
	/**
	 * Method handles request to display the grade card for student
	 * @param studentId
	 * @return
	 * @throws ValidationException
	 */
	
	@GET
	@Path("/{studentId}/grade")
	@Produces(MediaType.APPLICATION_JSON)
	public ReportCard viewGradeCard(
			@NotNull
			@PathParam("studentId") String studentId,
			@NotNull
			@Min(value = 1, message = "Student ID should not be less than 1")
			@Max(value = 9999, message = "Student ID should be less than 1000")
			@QueryParam("semester") int semester) throws ValidationException{
		
		
			ReportCard grade_card =null;
			try {
				grade_card = registrationInterface.viewReportCard(studentId,semester);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return grade_card;		
	}
	
}