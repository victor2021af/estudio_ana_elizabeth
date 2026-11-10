package br.com.estudioanaelizabeth.app.data;

import br.com.estudioanaelizabeth.app.BuildConfig;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/** Cliente HTTP único do app. A URL base muda entre debug e release (ver app/build.gradle). */
public final class ApiClient {

    private static ApiService service;

    private ApiClient() {
    }

    public static synchronized ApiService getService() {
        if (service == null) {
            service = new Retrofit.Builder()
                    .baseUrl(BuildConfig.API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiService.class);
        }
        return service;
    }
}
