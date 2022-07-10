package abs.servlets;

import abs.utils.ServletUtils;
import com.google.gson.Gson;
import dto.customerDTO.CustomerDTO;
import dto.database.AdminDatabase;
import dto.loanDTO.LoanDTO;
import engine.EngineImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "IncreaseYazServlet", urlPatterns = {"/increaseYaz"})
public class IncreaseYazServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer=resp.getWriter();
        EngineImpl engineManger = ServletUtils.getEngineManager(getServletContext());
        synchronized (this) {
            engineManger.advanceTime();
        }
        List<CustomerDTO> customers=engineManger.getAllCustomersDetails();
        List<LoanDTO> loans=engineManger.getAllLoans();
        AdminDatabase data=new AdminDatabase(customers,loans, engineManger.getCurrentYaz(), false);
        Gson gson=new Gson();
        writer.println(gson.toJson(data));
    }
}
