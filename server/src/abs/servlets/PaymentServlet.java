package abs.servlets;

import abs.utils.ServletUtils;
import com.google.gson.Gson;
import dto.customerDTO.CustomerDTO;
import engine.EngineImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;

import static abs.constants.Constants.USERNAME;
import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;

@WebServlet(name = "PaymentServlet", urlPatterns = {"/payment"})
public class PaymentServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String amountString=req.getParameter("amount");
        String requestData = req.getReader().lines().collect(Collectors.joining());
        Gson gson= new Gson();
        String name = (String) req.getSession().getAttribute(USERNAME);
        String loanID=gson.fromJson(requestData, String.class);
        String closeString=req.getParameter("close");
        EngineImpl engineManger= ServletUtils.getEngineManager(getServletContext());
        PrintWriter writer = resp.getWriter();
        if(closeString!= null) {
            synchronized (this){
                engineManger.closeLoan(loanID);
            }
        }
        else if(amountString!= null) {
            try {
                double amount=Double.parseDouble(amountString);
                synchronized (this) {
                    engineManger.payLoanRisk(loanID, amount);
                }
            }
            catch (NumberFormatException e) {
                writer.println("Invalid amount: the amount should be a number");
                resp.setStatus(SC_BAD_REQUEST);
                return;
            }
        }
        else {
            synchronized (this) {
                engineManger.payLoan(loanID);
            }
        }
        writer.println(gson.toJson(engineManger.createCustomerDTO(name)));
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
