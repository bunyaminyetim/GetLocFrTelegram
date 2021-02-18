/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package teleg;

import java.io.File;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import static org.openqa.grid.common.SeleniumProtocol.WebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * @author koray
 */
public class Teleg {

    /**
     * @param args the command line arguments
     */
    protected static String ChromePath = ".\\libs\\chromedriver.exe";
     // Chrome Sürücülerinize ulaşmanızı sağlayan servis
     protected static ChromeDriverService service;
     // Asıl sürücünüz ve Selenium size sağladığı en büyük nimet 
     // olan bulunduğunuz internet sayfasındaki 
     // elemanara(elementlere) ulaştığınız ve taracıyı 
     // kontrol ettiğiniz arkadaş
     protected static WebDriver driver;
     // Sizin için sürücünüzü bekletecek özellik(opsiyonel)
     protected static WebDriverWait wait;
     
    public static void main(String[] args) {
        // TODO code application logic here
      Excel ex=new Excel();
      Setup();
            
      wait(new Long(1000)); 
      
        try {
            while (true) {
                List<String[]> Data=new ArrayList<String[]>();
                List<String[]> Dataekle=new ArrayList<String[]>();
                while (java.time.LocalTime.now().getHour()<22) {
                   wait(new Long(60000)); 
                    System.out.println("bekle");
                }
                while (java.time.LocalTime.now().getHour()>21 ||(java.time.LocalTime.now().getHour()<7 )) {
                    System.err.println("çalışıyor");
                    Dataekle=parse();
                    for (String[] row : Dataekle) {
                        Data.add(row);
                    }
                }
                                
                ex.write(Data);
                                   
            }   
        } catch (Exception e) {
        }
        
      
    }
    
    public static boolean scroll_Top(WebElement webelement)
{
    try
    {               
        Actions dragger = new Actions(driver);
        // drag downwards
        int numberOfPixelsToDragTheScrollbarDown = 10;
        for (int i = 0; i < 1000; i+=10)
        {
            int a=webelement.getSize().height/2;
            dragger.moveToElement(webelement).clickAndHold().moveByOffset(0, -a).release().build().perform();
            System.out.println("scroll");
            //Thread.sleep(1);
        }
        
        return true;
    }
    catch (Exception e)
    {
        e.printStackTrace();
        return false;
    }
}
    
    public static List<String[]> parse(){
        
      List<WebElement> temizdiv=new ArrayList<WebElement>();
      WebElement element1=driver.findElement(By.className("im_history_selected_wrap"));
      WebElement element=element1.findElement(By.className("nano-pane"));
      scroll_Top(element);
      
      WebElement we=driver.findElement(By.xpath("//div[contains(@class,'m_history_col_wrap') and contains(@class, 'noselect') and contains(@class, 'im_history_loaded')]"));
  
      we=we.findElement(By.className("im_history_messages_peer"));
                 
      List<WebElement> maindiv = we.findElements(By.className("im_history_message_wrap"));
           
      //Collections.reverse(maindiv);
      String temizle;
      String temiz1;
      String date="";
     
      List<String[]> Data=new ArrayList<String[]>();
                
        for (int i=0; i<maindiv.size();i++) {
            WebElement md=maindiv.get(i);
            //ad, enlem, boylam, paylaşılan saat, paylaşılan gün, çekilen sistem saati
            String[] Row=new String[6];
            try {
                temizle=md.findElement(By.className("im_message_media")).getAttribute("innerHTML");
                
                temiz1=temizle.replaceAll("[^\\d.]+|\\.(?!\\d)", "");

                String Lat=temiz1.substring(temiz1.indexOf(".")-2,temiz1.indexOf(".")+14);
                String sub=temiz1.substring(temiz1.indexOf(".")+2, temiz1.length()-2);
                String Long=sub.substring(sub.indexOf(".")-2,sub.indexOf(".")+14);
                System.out.println(Lat+" , "+Long);
                Row[1]=Lat;
                Row[2]=Long;
                                       
                temizdiv.add(md);
                if(md.getText().contains("---")){
                    String TarihVeIsım=md.getText().substring(3,md.getText().length());
                    String Tarih=TarihVeIsım.substring(2,TarihVeIsım.indexOf("-"));
                    
                    date=Tarih;
                    
                    String SaatVeIsım=TarihVeIsım.substring(TarihVeIsım.indexOf("-")+2,TarihVeIsım.length());
                    System.out.println("Date"+date);
                    Row[4]=date;
                    Row[3]=SaatVeIsım.substring(3, 14);
                    Row[0]=SaatVeIsım.substring(15,SaatVeIsım.length()-1 );
                    System.out.println("Name"+Row[0]);
                    System.out.println("Time"+Row[3]);
                    Row[5]=LocalDateTime.now().toString();
                    
                }
                else{
                    System.out.println(md.getText());
                    Row[3]=md.getText().substring(1, 12);
                    Row[0]=md.getText().substring(13,md.getText().length()-1 );
                    Row[5]=LocalDateTime.now().toString();
                    Row[4]=date;
                }
                
            } catch (Exception e) {
                try {
                    date=md.findElement(By.className("im_message_date_split_text")).getAttribute("innerHTML");
                    System.out.println("hata "+ md.findElement(By.className("im_message_date_split_text")).getAttribute("innerHTML"));
                } catch (Exception er) {
                }
            }
            if(Objects.isNull(Row[0])){}
            else{
            Data.add(Row);}
        }
                
    
    return Data;
    }
    
    public static void wait(Long i){
    try {
          Thread.sleep(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void Setup(){
    // Chrome_driver.exe dizininden servisi oluştur ve başlat
    service = new ChromeDriverService.Builder().
              usingDriverExecutable(new File(ChromePath)).
              usingAnyFreePort().
              build();
     try {
         service.start();
     } catch (Exception e) {
         e.printStackTrace();
     }
    
     System.setProperty("webdriver.chrome.driver", ChromePath);
     // Driver nesnesini service bilgilerini kullanarak oluştur
     driver = new RemoteWebDriver(service.getUrl(),DesiredCapabilities.chrome());
     // Bütün tarayıcıyı açarken 15ms bekletir
     wait = new WebDriverWait(driver,15);
     // Penceri Büyüt
     driver.manage().window().maximize();
     //driver.get("file:///C:/Users/koray/Desktop/telegram%20deneme/Yeni%20klas%C3%B6r/Telegram%20Web.html");
     String link="https://web.telegram.org/#/im?p=g468368180";
     driver.get(link);
     wait(new Long(2000));
     while(driver.getCurrentUrl().equals("https://web.telegram.org/#/login")){
        wait(new Long(1000));
     }
     if(driver.getCurrentUrl().equals("https://web.telegram.org/#/im")){
         driver.get(link);
     }
         
    

}
    
    public static void Stop(){
     // Tarayıcıdan çıkış yapar
     driver.quit();
     // Servisi Durdurur
     service.stop();
}
       
}
