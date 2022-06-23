package abs.servlets;

import abs.utils.ServletUtils;
import com.google.gson.Gson;
import engine.EngineImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import static abs.constants.Constants.USERNAME;

@WebServlet(name = "withdrawServlet", urlPatterns = {"/withdraw"})
public class withdrawServlet extends HttpServlet {
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String amount=req.getHeader("amount");
        String name= (String) req.getSession().getAttribute(USERNAME);
        Integer withdraw=Integer.parseInt(amount);
        EngineImpl engineManger= ServletUtils.getEngineManager(getServletContext());
        synchronized (this){
            engineManger.withdraw(name,withdraw);
        }
        PrintWriter writer=resp.getWriter();
        Gson gson= new Gson();
        writer.println(gson.toJson(engineManger.createCustomerDTO(name)));
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
