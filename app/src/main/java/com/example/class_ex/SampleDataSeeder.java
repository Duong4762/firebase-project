package com.example.class_ex;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Ghi dữ liệu mẫu cố định ID (ghi đè nếu chạy lại). Cần user đã đăng nhập và rules cho phép ghi.
 */
public final class SampleDataSeeder {

    public static final String MOVIE_KONG = "m_kong_skull";
    public static final String MOVIE_MAI = "m_mai";
    public static final String MOVIE_DUNE = "m_dune2";
    public static final String MOVIE_INSIDE = "m_inside_out2";
    public static final String MOVIE_DEADPOOL = "m_deadpool_wolverine";
    public static final String THEATER_BTX = "t_bitexco";
    public static final String THEATER_CGV = "t_cgv_aeon";
    public static final String THEATER_LOTTE = "t_lotte_ct";

    private SampleDataSeeder() {}

    /** Đảm bảo mốc giờ luôn ở tương lai (lùi ngày nếu cùng ngày đã qua giờ đó). */
    private static long futureSlot(int dayOffsetFromToday, int hour, int minute) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, dayOffsetFromToday);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long t = c.getTimeInMillis();
        while (t <= System.currentTimeMillis()) {
            c.add(Calendar.DAY_OF_MONTH, 1);
            t = c.getTimeInMillis();
        }
        return t;
    }

    private static Map<String, Object> movie(
            String title, int durationMin, String genre, String description, String posterUrl
    ) {
        Map<String, Object> m = new HashMap<>();
        m.put("title", title);
        m.put("durationMin", durationMin);
        m.put("genre", genre);
        m.put("description", description);
        m.put("posterUrl", posterUrl);
        return m;
    }

    private static Map<String, Object> theater(String name, String address) {
        Map<String, Object> t = new HashMap<>();
        t.put("name", name);
        t.put("address", address);
        return t;
    }

    private static Map<String, Object> showtime(
            String movieId, String theaterId, long startTimeMillis, long price, int seats
    ) {
        Map<String, Object> s = new HashMap<>();
        s.put("movieId", movieId);
        s.put("theaterId", theaterId);
        s.put("startTimeMillis", startTimeMillis);
        s.put("price", price);
        s.put("seatsAvailable", seats);
        return s;
    }

    public static Task<Void> seed(FirebaseFirestore db) {
        WriteBatch b = db.batch();

        putMovie(b, db, MOVIE_KONG, movie(
                "Kong: Skull Island",
                118,
                "Hành động, Phiêu lưu",
                "Đội thám hiểm khám phá hòn đảo bí ẩn cai trị bởi Kong.",
                ""
        ));
        putMovie(b, db, MOVIE_MAI, movie(
                "Mai",
                131,
                "Tình cảm, Việt Nam",
                "Câu chuyện tình và gia đình lấy bối cảnh Sài Gòn.",
                ""
        ));
        putMovie(b, db, MOVIE_DUNE, movie(
                "Dune: Part Two",
                166,
                "Khoa học viễn tưởng",
                "Paul Atreides đoàn kết Fremen để chống lại nhà Harkonnen.",
                ""
        ));
        putMovie(b, db, MOVIE_INSIDE, movie(
                "Inside Out 2",
                96,
                "Hoạt hình",
                "Riley bước vào tuổi teen cùng các cảm xúc mới.",
                ""
        ));
        putMovie(b, db, MOVIE_DEADPOOL, movie(
                "Deadpool & Wolverine",
                128,
                "Hành động, Hài",
                "Deadpool và Wolverine cùng nhau vượt đa vũ trụ.",
                ""
        ));

        putTheater(b, db, THEATER_BTX, theater("CGV Bitexco", "Tầng 3, Bitexco Tower, Q.1, TP.HCM"));
        putTheater(b, db, THEATER_CGV, theater("CGV Aeon Mall Tân Phú", "30 Bờ Bao Tân Thắng, Q.Tân Phú"));
        putTheater(b, db, THEATER_LOTTE, theater("Lotte Cinema Cần Thơ", "Lô C, đường 3/2, Ninh Kiều, Cần Thơ"));

        int[][] slots = {
                {1, 10, 30}, {1, 14, 0}, {1, 17, 30}, {1, 19, 45}, {1, 21, 15},
                {2, 11, 0}, {2, 15, 20}, {2, 18, 0}, {2, 20, 30},
                {3, 9, 45}, {3, 13, 15}, {3, 16, 40}, {3, 19, 0}, {3, 22, 0},
        };
        String[] mids = {MOVIE_KONG, MOVIE_MAI, MOVIE_DUNE, MOVIE_INSIDE, MOVIE_DEADPOOL};
        String[] tids = {THEATER_BTX, THEATER_CGV, THEATER_LOTTE};
        long[] prices = {75_000L, 85_000L, 95_000L, 110_000L};
        int[] seatsOpts = {48, 60, 72, 40, 55};

        int st = 0;
        for (int[] slot : slots) {
            String movieId = mids[st % mids.length];
            String theaterId = tids[(st / 2) % tids.length];
            long price = prices[st % prices.length];
            int seats = seatsOpts[st % seatsOpts.length];
            long when = futureSlot(slot[0], slot[1], slot[2]);
            DocumentReference ref = db.collection("showtimes").document("st_demo_" + st);
            b.set(ref, showtime(movieId, theaterId, when, price, seats));
            st++;
        }

        return b.commit();
    }

    private static void putMovie(WriteBatch b, FirebaseFirestore db, String id, Map<String, Object> data) {
        b.set(db.collection("movies").document(id), data);
    }

    private static void putTheater(WriteBatch b, FirebaseFirestore db, String id, Map<String, Object> data) {
        b.set(db.collection("theaters").document(id), data);
    }
}
