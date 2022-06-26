package abs.servlets;

import abs.utils.ServletUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dto.FilterDTO;
import dto.customerDTO.CustomerDTO;
import dto.loanDTO.LoanDTO;
import engine.EngineImpl;
import engine.loan.Loan;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javafx.util.Pair;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

import static abs.constants.Constants.USERNAME;
@WebServlet(name = "ScrambleServlet", urlPatterns = {"/scramble"})
public class ScrambleServlet extends HttpServlet {
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EngineImpl engineManger = ServletUtils.getEngineManager(getServletContext());
        String name = (String) req.getSession().getAttribute(USERNAME);
        PrintWriter writer = resp.getWriter();
        try {
            int amount = Integer.parseInt(req.getParameter("amount"));
            int maximumOwnership=Integer.parseInt(req.getParameter("ownership"));
            Gson gson = new Gson();
            Type listType = new TypeToken<List<String>>() {
            }.getType();
            String requestData = req.getReader().lines().collect(Collectors.joining());
            List<String> filteredLoans = gson.fromJson(requestData, listType);
            Type pairType = new TypeToken<Pair<Double, CustomerDTO>>() {
            }.getType();
            double res;

            synchronized (this) {
                res=engineManger.assign(filteredLoans, amount, name, maximumOwnership);
            }
            writer.println(gson.toJson(new Pair<Double, CustomerDTO>(res, engineManger.createCustomerDTO(name)), pairType));

            resp.setStatus(HttpServletResponse.SC_OK);
        }
        catch (NumberFormatException e) {
            writer.println("");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
