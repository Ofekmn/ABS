package abs.servlets;

import abs.utils.ServletUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dto.FilterDTO;
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
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static abs.constants.Constants.USERNAME;
@WebServlet(name = "FilterServlet", urlPatterns = {"/filter"})
public class FilterServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EngineImpl engineManger = ServletUtils.getEngineManager(getServletContext());
        String name = (String) req.getSession().getAttribute(USERNAME);
        Gson gson = new Gson();
        String requestData = req.getReader().lines().collect(Collectors.joining());
        FilterDTO filterDTO = gson.fromJson(requestData, FilterDTO.class);
        List<LoanDTO> filteredLoans;
        synchronized (this) { //not sure if synchronized is a must...
            filteredLoans=engineManger.filteredLoans(name,filterDTO.getCategories(),filterDTO.getInterest(),filterDTO.getYaz(),filterDTO.getMaximumOpenLoans());
        }
        PrintWriter writer = resp.getWriter();
        Type listType = new TypeToken<List<LoanDTO>>() {}.getType();
        writer.println(gson.toJson(filteredLoans, listType));
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
