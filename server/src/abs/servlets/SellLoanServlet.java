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

@WebServlet(name = "SellLoanServlet", urlPatterns = {"/sellLoan"})
public class SellLoanServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String loanID=req.getParameter("ID");
        Gson gson= new Gson();
        String name = (String) req.getSession().getAttribute(USERNAME);
        EngineImpl engineManger= ServletUtils.getEngineManager(getServletContext());
        synchronized (this) {
            engineManger.sellLoan(loanID, name);
        }
        PrintWriter writer=resp.getWriter();
        writer.println(gson.toJson(engineManger.createCustomerDTO(name)));
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
