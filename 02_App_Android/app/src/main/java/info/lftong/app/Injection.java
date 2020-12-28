package info.lftong.app;

import android.content.Context;

import info.lftong.data.DataRepository;
import info.lftong.data.LocalDataSource;
import info.lftong.data.RemoteDataSource;


/**
 * Created by Administrator on 2017/9/25.
 */

public class Injection {
    public static DataRepository provideTasksRepository(Context context) {
        return DataRepository.getInstance(RemoteDataSource.getInstance(), LocalDataSource.getInstance(context));
    }
}
