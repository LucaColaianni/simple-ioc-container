package asciiart;

public class AsciiBanner {

    public static void asciiBanner(){
        String banner = """
                 _____      _____   _____             _        _                \s
                |_   _|    /  __ \\ /  __ \\           | |      (_)               \s
                  | |  ___ | /  \\/ | /  \\/ ___  _ __ | |_ __ _ _ _ __   ___ _ __\s
                  | | / _ \\| |     | |    / _ \\| '_ \\| __/ _` | | '_ \\ / _ \\ '__|
                 _| || (_) | \\__/\\ | \\__/\\ (_) | | | | || (_| | | | | |  __/ |  \s
                 \\___/\\___/ \\____/  \\____/\\___/|_| |_|\\__\\__,_|_|_| |_|\\___|_| \s
                                :: Started! ::
                """;

        System.out.println(banner);
    }
}
