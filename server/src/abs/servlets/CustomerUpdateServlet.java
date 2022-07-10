package abs.servlets;

import abs.utils.ServletUtils;
import com.google.gson.Gson;
import dto.customerDTO.CustomerDTO;
import dto.database.CustomerDatabase;
import engine.EngineImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static abs.constants.Constants.USERNAME;

@WebServlet(name = "CustomerUpdateServlet", urlPatterns = {"/customerUpdate"})
public class CustomerUpdateServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer=resp.getWriter();
        String name = (String) req.getSession().getAttribute(USERNAME);
        EngineImpl engineManger = ServletUtils.getEngineManager(getServletContext());
        CustomerDTO customer=engineManger.createCustomerDTO(name);
        List<String> categories=engineManger.getCategories();
        boolean isRewind=engineManger.isRewind();
        if(customer==null && isRewind)
            customer=new CustomerDTO(name);
        CustomerDatabase data= new CustomerDatabase(customer, categories, engineManger.getRewindStateYaz(), isRewind);
        Gson gson = new Gson();
        writer.println(gson.toJson(data));
        writer.flush();
    }
}
