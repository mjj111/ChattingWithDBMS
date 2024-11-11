package shardingTest.lab.mysql.config;

public class ThreadLocalDatabaseContextHolder {
    private static final ThreadLocal<Long> CONTEXT = new ThreadLocal<>();

    public static void setRoomId(Long roomId) {
        CONTEXT.set(roomId);
    }

    public static Long popRoomId() {
        Long result = CONTEXT.get();
        CONTEXT.remove();
        return  result;
    }
}