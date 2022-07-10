package admin;

import com.google.gson.reflect.TypeToken;
import dto.customerDTO.CustomerDTO;
import dto.loanDTO.LoanDTO;
import javafx.application.Platform;
import javafx.util.Pair;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.HttpClientUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

import static util.Constants.ADMIN_UPDATE_PAGE;
import static util.Constants.GSON_INSTANCE;

public class AdminRefresher extends TimerTask {
    private final Consumer <Pair<List<CustomerDTO>, List<LoanDTO>>> systemConsumer;

    public AdminRefresher(Consumer <Pair<List<CustomerDTO>, List<LoanDTO>>> systemConsumer) {
        this.systemConsumer = systemConsumer;
    }

    @Override
    public void run() {
        Request request = new Request.Builder()
                .url(ADMIN_UPDATE_PAGE)
                .get()
                .build();
        HttpClientUtil.runAsync(request, new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                Type pairType = new TypeToken<Pair<List<CustomerDTO>, List<LoanDTO>>>() {
                }.getType();
                Platform.runLater(() -> {
                    Pair<List<CustomerDTO>, List<LoanDTO>> systemUpdate = GSON_INSTANCE.fromJson(responseBody, pairType);
                    systemConsumer.accept(systemUpdate);
                });
            }
        });
    }
}
