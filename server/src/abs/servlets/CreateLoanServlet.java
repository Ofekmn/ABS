package abs.servlets;

import abs.utils.ServletUtils;
import com.google.gson.Gson;
import dto.loanDTO.LoanDTO;
import engine.EngineImpl;
import engine.exception.xml.NameException;
import engine.exception.xml.YazException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;

import static abs.constants.Constants.USERNAME;

@WebServlet(name = "CreateLoanServlet", urlPatterns = {"/createLoan"})
public class CreateLoanServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EngineImpl engineManger = ServletUtils.getEngineManager(getServletContext());
        String name = (String) req.getSession().getAttribute(USERNAME);
        Gson gson = new Gson();
        String requestData = req.getReader().lines().collect(Collectors.joining());
        LoanDTO loan = gson.fromJson(requestData, LoanDTO.class);
        try {
            synchronized (this) {
                engineManger.createNewLoan(name, loan.getId(), (int) loan.getAmount(), loan.getCategory(), loan.getPaysEveryYaz(), (int) loan.getInterestPerPayment()
                        , loan.getTotalYaz());
            }
            PrintWriter writer = resp.getWriter();
            writer.println(gson.toJson(engineManger
                    .createCustomerDTO(name)));
            resp.setStatus(HttpServletResponse.SC_OK);

        } catch (YazException e) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getOutputStream().print(e.getMessage());
        } catch (NameException e) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getOutputStream().print(e.getMessage());
        }
    }
}