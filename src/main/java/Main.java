import com.fasterxml.jackson.databind.node.ObjectNode;
import design.Color;
import requests.Request;
import threading.Threading;


import java.io.*;
import java.net.http.HttpResponse;
import java.util.*;

public class Main implements Color {
    //rv
    final static String PROGRAM_NAME = "Email Checker";
    static Scanner input = new Scanner(System.in);
    static boolean running = true;
    static List<String> user_email_list = new ArrayList<>();
    static Iterator<String> it;
    static List<String> proxies = new ArrayList<>();
    static int good, error, bad, tryAgain;


    public static void main(String[] args) {
        //checkIP();
        System.out.println(YELLOW+"\n#"+PROGRAM_NAME+RESET);
        System.out.println();
        loadEmailsList();
        loadProxies();

        it = user_email_list.iterator();
        System.out.println("Enter threads: ");
        int threads_count = input.nextInt(); input.nextLine();

        List<Threading> threads_list = new ArrayList<>();

        for (int i=0; i<threads_count; i++) {
            threads_list.add(new Threading() {
                @Override
                public void run() {
                    Main.run();
                }
            });
        }
        threads_list.add(new Threading() {
            @Override
            public void run() {
                Main.counter();
            }
        });
        Threading.startAll(threads_list);
    }

    public static void loadProxies() {
        try {
            FileReader fr = new FileReader("proxies.txt");
            BufferedReader br = new BufferedReader(fr);

            String line = br.readLine();
            while (line!=null) {
                proxies.add(line);
                line = br.readLine();
            }
        }
        catch (IOException e) {
            System.out.println(RED+"proxies.txt is missing."+RESET);
            input.nextLine();
            System.exit(0);
        }
    }
    public static void loadEmailsList() {
        try {
            FileReader fr = new FileReader("emails.txt");
            BufferedReader br = new BufferedReader(fr);

            String line = br.readLine();
            while (line!=null) {
                user_email_list.add(line);
                line = br.readLine();
            }
        }
        catch (IOException e) {
            System.out.println(RED+"emails.txt is missing."+RESET);
            input.nextLine();
            System.exit(0);
        }
    }

    public static void writeToFile(String file, String str) {
        PrintWriter writer;
        try {
            writer = new PrintWriter(new FileWriter(file, true));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writer.println(str);
        writer.close();
    }

    public static void sleepFor(int seconds) {
        try {
            Thread.sleep(seconds*1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public static void run() {
        while (running) {
            if (!it.hasNext()) {
                running = false;
                break;
            }
            String user_email = it.next();
            checkEmail(user_email);
        }
    }

    public static void counter() {
        int rs = 0;
        while (running) {
            int before = good+bad+tryAgain;
            System.out.print("\rgood: "+good+" || bad: "+bad+" || tryAgain: "+tryAgain+" || error: "+error+" || rs: "+rs);
            sleepFor(1);
            int after = good+bad+tryAgain;
            rs = after-before;
        }
        System.out.println(GREEN+"\nFinished."+RESET);
    }
    public static void checkEmail(String user_email) {
        if (!user_email.contains(":") || !user_email.contains("@")) return;
        String[] splitted = user_email.split(":");
        String username = splitted[0];
        String email = splitted[1];

        Request request = new SendResetRequest(username).getRequest();
        try {
            String proxy = proxies.get(Generator.getRandomNumber(0, proxies.size()));
            String proxy_ip = proxy.split(":")[0];
            int proxy_port = Integer.parseInt(proxy.split(":")[1]);
            request.setProxy("HTTP", proxy_ip, proxy_port);
            //request.setTimeout(10);
        } catch (Exception e) {
            //e.printStackTrace();
            checkEmail(user_email);
            return;
        }

        HttpResponse<String> response;
        ObjectNode requestJson;
        try {
            response = request.send();
            requestJson = Request.convertToJson(response.body());
        }
        catch (Exception e) {
            //System.out.println(e.getMessage());
            checkEmail(user_email);
            return;
        }

        //System.out.println(response.body());
        if (response.statusCode()==200) {
            String obfuscated_email = requestJson.get("obfuscated_email").asText();
            String email_username = email.split("@")[0];
            String stChar = String.valueOf(email_username.charAt(0));
            String lastChar = String.valueOf(email_username.charAt(email_username.length()-1));

            String obfuscated_email_username = obfuscated_email.split("@")[0];
            String ob_stChar = String.valueOf(obfuscated_email_username.charAt(0));
            String ob_lastChar = String.valueOf(obfuscated_email_username.charAt(obfuscated_email_username.length() - 1));
            //System.out.println(stChar+" "+ob_stChar+" "+lastChar+" "+ob_lastChar);
            if (stChar.equals(ob_stChar) && (lastChar.equals(ob_lastChar) || lastChar.equals("*"))) {
                good++;
                writeToFile("good.txt", user_email);

                //System.out.println(GREEN+user_email+RESET);
                return;
            }
            bad++;
            writeToFile("bad.txt", username + ":"+email+":"+obfuscated_email);

            //System.out.println(YELLOW+user_email+" -> "+obfuscated_email+RESET);
        }
        else if (response.statusCode()==400 || response.statusCode()==429) {
            if (response.body().contains("rate_limit_error")) {
                checkEmail(user_email);
                return;
            }
            //System.out.println(response.body());
            tryAgain++;
            writeToFile("tryAgain.txt",user_email);
            //System.out.println(PURPLE+user_email+" -> "+response.statusCode()+RESET);
        }
        else if (response.statusCode()==404) {
            error++;
            writeToFile("notFound.txt",user_email);
        }
        else {
            error++;
//            System.out.println(response.body());
//            System.out.println(RED+user_email+" -> "+response.statusCode()+RESET);
        }
        //(new Scanner(System.in)).nextLine();
    }

}
