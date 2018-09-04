package com.djinggamedia.damakesmas.app;

public class AppConfig {


    public static final String URL_DRIVER_IMAGE ="http://system.avillahospitality.com/" ;
    public static final String URL_IMAGE = "http://system.avillahospitality.com/";
    public static final String GLOBAL = "http://damakesmas.denpasarkota.go.id";

    private  static String  general = "https://pengepulkopi.000webhostapp.com/pengepul/";
    public static final String URL_PENGEPUL = general+"getPengepul.php";

    public  static String getUrlLogin(String username, String password, String token)
    {
       String username2= username.replace(" ","%20");
        String password2=password.replace(" ","%20");
        return GLOBAL+"/index.php?pagetype=service&user=login&username="+username2+"&pass="+password2+"&regid="+token;
    }

    public static String getUrlProfile(String tiket)
    {
        tiket.replace(" ","%20");
        return GLOBAL+"/index.php?language=id&domain=&pagetype=service&page=Web-Service-Register-Pasien&action=getProfilPegawai&tiket="+tiket;
    }


    public static String getEmergencyJadwal(String tiket) {
        return GLOBAL+"/index.php?language=id&domain=&pagetype=service&page=Web-Service-Register-Pasien&action=getEmergencyPasienList&tiket="+tiket;
    }

    public static String getURLJadwal(String tiket,String keyword,String filter) {
        return GLOBAL+"/index.php?language=id&domain=&pagetype=service&page=Web-Service-Register-Pasien&action=getPenjadwalanPasienList&tiket="+tiket+"&keyword="+keyword+"&tanggal="+filter;
    }

    public static String getWebURLbyId(String tiket,String id) {
        return "http://damakesmas.denpasarkota.go.id/?domain=&page=Pasien-Terdaftar&language=id&tab=riwayatrawatjalan&action=viewformaddrawatjalan&tiket="+tiket+"&id="+id;
    }

    public static String getWebURLbyRegister(String tiket,String register) {
        return "http://damakesmas.denpasarkota.go.id/index.php?page=202&language=id&domain=&tab=riwayatrawatjalan&id=1&action=viewdetailrawatjalan&tiket="+tiket+"&noregister="+register;
    }

    public static String getUrlTandai(String tiket,String norm,String lat,String lng) {
        return "http://damakesmas.denpasarkota.go.id/index.php?language=id&domain=&pagetype=service&page=Web-Service-Register-Pasien&action=updateLokasiPasien&tiket="+tiket+"&norm="+norm+"&lat="+lat+"&lng="+lng;
    }

    public static String getUrlGPS(String tiket) {
        return "http://damakesmas.denpasarkota.go.id/index.php?language=id&domain=&pagetype=service&page=Web-Service-Register-Pasien&action=getGPSDeviceList&tiket="+tiket;
    }
}
