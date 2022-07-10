package abs.servlets;

import abs.utils.ServletUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dto.customerDTO.CustomerDTO;
import dto.loanDTO.LoanDTO;
import engine.EngineImpl;
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

import static abs.constants.Constants.USERNAME;

@WebServlet(name = "AdminUpdateServlet", urlPatterns = {"/adminUpdate"})
public class AdminUpdateServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer=resp.getWriter();
        EngineImpl engineManger = ServletUtils.getEngineManager(getServletContext());
        List<CustomerDTO> customerList=engineManger.getAllCustomersDetails();
        List<LoanDTO> loanList=engineManger.getAllLoans();
        Pair<List<CustomerDTO>, List<LoanDTO>> system=new Pair<>(customerList,loanList);
        Type pairType = new TypeToken<Pair<List<CustomerDTO>, List<LoanDTO>>>() {
        }.getType();
        Gson gson = new Gson();
        writer.println(gson.toJson(system, pairType));
        writer.flush();
    }
}
