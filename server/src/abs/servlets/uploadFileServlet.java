package abs.servlets;

import abs.utils.ServletUtils;
import com.google.gson.Gson;
import engine.EngineImpl;
import engine.exception.xml.LoanFieldDoesNotExist;
import engine.exception.xml.NameException;
import engine.exception.xml.YazException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Scanner;
import java.util.stream.Collectors;

import static abs.constants.Constants.USERNAME;

@WebServlet(name = "uploadFileServlet", urlPatterns = {"/upload-file"})
@MultipartConfig
public class uploadFileServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Part part = req.getPart("file1");
        InputStream file= part.getInputStream();
        EngineImpl engineManger=ServletUtils.getEngineManager(getServletContext());
        String name= (String) req.getSession().getAttribute(USERNAME);
        try {
            synchronized (this) {
//                String result = new BufferedReader(new InputStreamReader(file))    .lines().collect(Collectors.joining("\n"));
                engineManger.loadFile(file, name);
            }
            PrintWriter writer=resp.getWriter();
            Gson gson= new Gson();
            writer.println(gson.toJson(engineManger
                    .createCustomerDTO(name)));
            resp.setStatus(HttpServletResponse.SC_OK);

        } catch (NameException e) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getOutputStream().print(e.getMessage());
        } catch (LoanFieldDoesNotExist e) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getOutputStream().print(e.getMessage());
        } catch (YazException e) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getOutputStream().print(e.getMessage());
        }
    }
}
