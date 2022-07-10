package customer;

import dto.customerDTO.CustomerDTO;
import dto.database.CustomerDatabase;
import dto.loanDTO.LoanDTO;
import javafx.application.Platform;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.HttpClientUtil;

import java.io.IOException;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

import static util.Constants.CUSTOMER_UPDATE_PAGE;
import static util.Constants.GSON_INSTANCE;

public class CustomerRefresher extends TimerTask {

    private final Consumer<CustomerDatabase> customerConsumer;
    private Consumer<List<LoanDTO>> filteredLoansConsumer;

    public CustomerRefresher(Consumer<CustomerDatabase> customerConsumer) {
        this.customerConsumer = customerConsumer;
    }

    @Override
    public void run() {
        Request request = new Request.Builder()
                .url(CUSTOMER_UPDATE_PAGE)
                .get()
                .build();
        HttpClientUtil.runAsync(request, new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                Platform.runLater(() -> {
                    CustomerDatabase data = GSON_INSTANCE.fromJson(responseBody, CustomerDatabase.class);
                    customerConsumer.accept(data);
                });
            }
        });
    }
}
