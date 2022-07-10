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
import java.util.stream.Collectors;

@WebServlet(name = "RewindServlet", urlPatterns = {"/rewind"})
public class rewindServlet extends HttpServlet {
    //check that Admin is the one accessing the servlet...

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer=resp.getWriter();
        EngineImpl engineManger = ServletUtils.getEngineManager(getServletContext());
        Gson gson = new Gson();
        String requestData = req.getReader().lines().collect(Collectors.joining());
        int rewindYaz = gson.fromJson(requestData, Integer.class);
        synchronized (this) {
            engineManger.rewind(rewindYaz);
        }
        boolean isRewind=engineManger.isRewind();
        resp.setStatus(HttpServletResponse.SC_OK);
        List<CustomerDTO> customers=engineManger.getAllCustomersDetails();
        List<LoanDTO> loans=engineManger.getAllLoans();
        AdminDatabase data=new AdminDatabase(customers,loans, engineManger.getCurrentYaz(), isRewind);
        writer.println(gson.toJson(data));
    }
}
