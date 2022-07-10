package abs.servlets;

import abs.utils.ServletUtils;
import com.google.gson.Gson;
import engine.EngineImpl;
import engine.exception.money.NotEnoughMoneyException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import static abs.constants.Constants.USERNAME;

@WebServlet(name = "BuyLoanServlet", urlPatterns = {"/buyLoan"})
public class BuyLoanServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer=resp.getWriter();
        String loanID=req.getParameter("ID");
        Gson gson= new Gson();
        String name = (String) req.getSession().getAttribute(USERNAME);
        EngineImpl engineManger= ServletUtils.getEngineManager(getServletContext());
        try {
            synchronized (this) {
                engineManger.buyLoan(loanID, name);
            }
            writer.println(gson.toJson(engineManger.createCustomerDTO(name)));
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        catch(NotEnoughMoneyException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            writer.println(e.getMessage());
        }
    }
}
