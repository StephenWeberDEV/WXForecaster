/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Beans.Forecast;
import Beans.User;
import DB.ForecastDB;
import DB.UserDB;
import Util.MailUtil;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Stephen
 */
@WebServlet(name = "ForecastController", urlPatterns = {"/forecast"})
public class ForecastController extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        String action = request.getParameter("action");
        User user = new User();
        User admin = new User();
        user = (User) session.getAttribute("theUser");
        admin = (User) session.getAttribute("theUser");
        
        String url = "/home.jsp";
        
        if(action == null){
            action = "join";
            url="/main.jsp";
        }
        if(action.equals("join")){
            url = "/home.jsp";
        }
        
        if(action.equals("add")){
            String msg = "";
            Forecast forecast = new Forecast();
            int high = Integer.parseInt(request.getParameter("highTemp"));
            int low = Integer.parseInt(request.getParameter("lowTemp"));
            int wnd = Integer.parseInt(request.getParameter("windSpd"));
            String sky = request.getParameter("skyCond");
            double precip = Double.parseDouble(request.getParameter("precip"));
            
            if(user != null){
                forecast.setHighTemp(high);
                forecast.setLowTemp(low);
                forecast.setWindSpeed(wnd);
                forecast.setSkyConditions(sky);
                forecast.setPrecip(precip);
                forecast.setForecasterEmail(user.getEmail());
                forecast.setStatus("Pending");
                Date date = new Date();
                forecast.setDateSubmitted(date);
                
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                StringBuilder sb = new StringBuilder();
//                sb.append(month);
//                sb.append("-"+day);
                sb.append(date + " ");
                sb.append(user.getEmail());
                String id = sb.toString();
                forecast.setForecastID(id);
                
//                List<Forecast> forecasts;
//                forecasts = ForecastDB.getForecasts(user.getEmail());
//                if(forecasts != null){
//                    for(Forecast f : forecasts){
//                        if(f.getDateSubmitted().toString().equals(id)){
//                            msg = "Sorry but you've already submitted a forecast today! Try again tomorrow!";
//                            url="/forecasts.jsp";
//                        }else{
                            ForecastDB.insert(forecast);
//                        }
//                    }
//                }

                url="/forecast.jsp";
                
                
            }
            
        }
        
        if(action.equals("email")){
            String highTemp = request.getParameter("highTemp");
            String lowTemp = request.getParameter("lowTemp");
            String windSpd = request.getParameter("windSpd");
            String sky = request.getParameter("skyCond");
            String precip = request.getParameter("precip");
            String toAddr = request.getParameter("toAddr");
            //TODO: Add rest of parameters!
            
            Forecast forecast = ForecastDB.getForecast(user.getEmail()); 
            //Sending email need to add more code above..
            String to = toAddr;
            String from = user.getEmail();
            String subject = "Check out this Charlotte area forecast!!";
            String body 
                    = "Hi, <br/>"
                    + "Your 24 hour forecast for Charlotte, NC starting tomorrow at 12:00 am EDT <br/><br/>"
                    + "High: " + highTemp + "<br/>" + "Low: " + lowTemp + "<br/>"
                    + "Winds: " + windSpd + "<br/>" + "Skies: " + sky + "<br/>"
                    + "Precip (Inches): " + precip;
            
            boolean isBodyHTML = true;
            
            try{
                MailUtil.sendMail(to, from, subject, body,isBodyHTML);
            }catch(MessagingException e){
                String errorMessage
                        = "Error: Unable to send email. "
                        + "Check Tomcat logs for details. <br/> ";
                request.setAttribute("errorMessage",errorMessage);
            }
            url="/forecast.jsp";
        }
        
        if(action.equals("admin")){
            List<Forecast> forecasts = ForecastDB.getForecasts();
            request.setAttribute("submittedForecasts",forecasts);
            url="/admin.jsp";
        }
        
    getServletContext()
                .getRequestDispatcher(url)
                .forward(request,response);      
       
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        doPost(request,response);
    }

}
