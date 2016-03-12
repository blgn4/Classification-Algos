package classifier;

/**
 * Created by Niharika on 3/19/15.
 */


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.asu.sml.data.DiabetesSisters;


public class NaiveBayes {

	public static void main(String[] args) {
	    DiabetesSisters diabetesSisters = DiabetesSisters.getInstance();
	    NaiveBayes nb = new NaiveBayes();    		
	    Map<String,List<String>> trainingData = diabetesSisters.getTrainingData();
	    Map<String,List<String>> testData = diabetesSisters.getTestData();
	    Map<String,List<String>> completeData = diabetesSisters.getCompleteData();
	    
	/*    Map<String,List<String>> trainingData = nb.removeStopWords(trainingData1);
	    Map<String,List<String>> testData = nb.removeStopWords(testData1);
	    Map<String,List<String>> completeData = nb.removeStopWords(completeData1);*/
	    
	    int totaltest=0;
	    int correctresult=0;
	    
	    Set<String> V = nb.getVocabulary(completeData,trainingData); 
	    List<HashMap<String, Double>> condProb = nb.getCondprob(trainingData ,V);
	    Map<String , Double> Prior = nb.getPrior(completeData);
	    
	    Map<String , Integer> Classvalues = new HashMap<String, Integer>();
	    String[] name = new String[4];
	    int j = 0;  
	    for(String key: Prior.keySet())
		  {
			name[j] = key;  
			j++;
		  }
	    Classvalues.put(name[0],1);
	    Classvalues.put(name[1],2);
	    Classvalues.put(name[2],3);
	    Classvalues.put(name[3],4);
	    
	    for(String key: testData.keySet())
	    {
	    	List<String> instances = testData.get(key);
	    	String classname = key;
	    	
	    	for(String instance:instances)
	    	{
	    		double[] scores = new double[4] ;
	    		int i=0;
	    		for(String key1:Prior.keySet())
	    		{
	    			scores[i] = nb.nBtest(instance, Prior.get(key1), condProb.get(i));
	    			i++;
	    		}
	    		int classno = nb.maxim(scores);
	    		
	    		if(classno == Classvalues.get(classname))
	    		correctresult++;
	    		
	    		totaltest ++;
	    		
	    	}
	    }
	    
	    System.out.println("A total of "+ correctresult+"correct results of "+totaltest+"number of given data");
	    

	  }
	/*  public String convertToname(Map<String , Double> Prior ,int classno)
	  {
		  int i = 0;
		  String[] name = null;
		  for(String key: Prior.keySet())
		  {
			name[i] = key;  
		  }
		  if(classno == 1)
		  return name[0];
		  
		  else if(classno == 2)
			  return name[1];
		  else if(classno == 3)
			  return name[2];
		  else if(classno == 4)
			  return name[3];
		  else 
			  return "No class";
	  }*/
	
	public Map<String , List<String>> removeStopWords(Map<String, List<String>> Data)
	{
		Map<String,List<String>> newData = new HashMap<String , List<String>>();
		List<String> rows = new ArrayList<String>();
		String eachline = new String();
	    try{
	        URI swUri = DiabetesSisters.class.getResource("/Stopwords").toURI();
	        File swDirectory = new File(swUri);
            
	        BufferedReader br = new BufferedReader(new FileReader(swDirectory));
	       
	        	for(String key:Data.keySet())
	        	{
	        		for(String dat:Data.get(key))
	        		{
	        			
	        			System.out.println(dat);
	        	     String lines = br.readLine();
	        		 while(lines!=null)
	        		  {
	        			//System.out.println(Data.size());
	        			String[] wrd = dat.split(" ");
	        			System.out.println(wrd.length);
	        			for(int i=0 ; i<wrd.length ; i++ )
	        			{
	        			if(!lines.equals(wrd[i]))
	        			eachline+= wrd[i];	
	        			}
	        			System.out.println(eachline);
	        			rows.add(eachline);
	        		}
	        		}
	        		newData.put(key, rows);
	        	}
	        	
	        

	      }catch (URISyntaxException e) {
	        System.out.println("Couldn't find the Diabetes Sisters Directory.");
	        e.printStackTrace();
	      }catch (Exception e){
	        e.printStackTrace();
	      }
	    return newData;
	}
	  
	  public int maxim (double[] scores)
	  {
		 if(scores[0] >= scores[1] && scores[0] >= scores[2] && scores[0] >= scores[3]  )
		 {
			 return 1;
		 }
		 else if(scores[2] >= scores[1] && scores[2] >= scores[3] && scores[2] >= scores[0] )
		 {
			 return 3;
		 }
		 else if(scores[3] >= scores[1] && scores[3] >= scores[2] && scores[3] >= scores[0]  )
		 {
			 return 4;
		 }
		 else if(scores[1] >= scores[2] && scores[1] >= scores[2] && scores[1] >= scores[3]  )
		 {
			 return 2;
		 }
		 else 
			 return -1;
	  }
	  
	  public Set<String> getVocabulary(Map<String, List<String>> completeData , Map<String, List<String>> trainingData )
	  {
		  Set<String> Vocab = new HashSet();
		 // Set<String> newVocab = new HashSet();
		  for(String key:completeData.keySet())
		  {
			  List<String> instanceList = completeData.get(key);
			  for(String instance:instanceList)
			  {
				  String[] eachword = instance.split(" ");
				  for(int i = 0 ; i<eachword.length ; i++)
				  {
					  
					  Vocab.add(eachword[i]);
				  }
			  }
			  
		  }
		 // System.out.println(Vocab.size());
		 try{
		        URI swUri = DiabetesSisters.class.getResource("/Stopwords").toURI();
		        File swDirectory = new File(swUri);
	            
		        BufferedReader br = new BufferedReader(new FileReader(swDirectory));
		        String lines = new String();
		        while((lines = br.readLine())!=null)
		        {
		        String[] line = lines.split(" ");
		        
		        	Vocab.remove(line[0]);
		        }
		        
		  }
		  catch (URISyntaxException e) {
		        System.out.println("Couldn't find the Directory.");
		        e.printStackTrace();
		      }catch (Exception e){
		        e.printStackTrace();
		      }
		  return Vocab;
		  
	  }
	  
	  public List<HashMap<String, Double>> getCondprob( Map<String, List<String>> trainingData , Set<String> V )
	  {
		  int categ =0;
		 List<HashMap<String, Double>> wordcounts = new ArrayList<HashMap<String,Double>>();
		  for(List<String> instances:trainingData.values())
		  {
			  Map<String ,Double> wcount = new HashMap<String,Double>();
			  for(String instance:instances)
			  {
				  String[] word = instance.split(" ");
				  for(int i=0 ;i<word.length;i++)
				  {
					  if(wcount.containsKey(word[i]) )
					  {
						  double val = wcount.get(word[i]);
						  wcount.put(word[i], val);
					  }
					  else
						  wcount.put(word[i], 1.00);
				  }
			  }
			  try{
			        URI swUri = DiabetesSisters.class.getResource("/Stopwords").toURI();
			        File swDirectory = new File(swUri);
		            
			        BufferedReader br = new BufferedReader(new FileReader(swDirectory));
			        String lines = new String();
			        while((lines = br.readLine())!=null)
			        {
			        String[] line = lines.split(" ");
			        
			        	wcount.remove(line[0]);
			        }
			        
			  }
			  catch (URISyntaxException e) {
			        System.out.println("Couldn't find the Directory.");
			        e.printStackTrace();
			      }catch (Exception e){
			        e.printStackTrace();
			      }
			  for(String vocab:V)
			  {
				  if(wcount.containsKey(vocab))
					  wcount.put(vocab, wcount.get(vocab)+1);
				  else
					  wcount.put(vocab, 1.0);
			  }
			  
			  wordcounts.add((HashMap<String, Double>) wcount);
		  }
		  List<HashMap<String , Double>> condprob = new ArrayList<HashMap<String,Double>>();
		  for(int i=0;i<4;i++)
		  {
			  Map<String,Double> cprob = new HashMap<String,Double>();
			  double total=0.00;
			for(String key:wordcounts.get(i).keySet())
			{
				total+=wordcounts.get(i).get(key);
			}
			
			for(String key:wordcounts.get(i).keySet())
			{
				cprob.put(key, (wordcounts.get(i).get(key)/total));
			}
			condprob.add((HashMap<String, Double>) cprob);
			
		  }
		  
		return condprob;  
	  }
	  
	  public Map<String , Double> getPrior(Map<String, List<String>> completeData )
	  {
		  Map<String , Double> Prior = new HashMap();
		  double totval =0;
		  int i=0;
		  for(String key:completeData.keySet())
		  {
			  List<String> instances = completeData.get(key);
			  totval+= instances.size();
		  }
		  
		  for(String key:completeData.keySet())
		  {
			  Prior.put(key, (completeData.get(key).size()/totval));
		  }
		  
		  return Prior;
	  }
	  
	  public double nBtest(String text, Double prior , Map<String, Double> condProb)
	  {
		  
		  String category;
		  double Score = Math.log(prior);
		  Set<String> wrds= new HashSet();
		  String[] words = text.split(" ");
		  for(int i=0 ; i<words.length ; i++)
		  {
			  wrds.add(words[i]);
		  }
		  try{
		        URI swUri = DiabetesSisters.class.getResource("/Stopwords").toURI();
		        File swDirectory = new File(swUri);
	            
		        BufferedReader br = new BufferedReader(new FileReader(swDirectory));
		        String lines = new String();
		        while((lines = br.readLine())!=null)
		        {
		        String[] line = lines.split(" ");
		        
		        	wrds.remove(line[0]);
		        }
		        
		  }
		  catch (URISyntaxException e) {
		        System.out.println("Couldn't find the Directory.");
		        e.printStackTrace();
		      }catch (Exception e){
		        e.printStackTrace();
		      }
		  for(String token:wrds)
		  {
			Score += Math.log(condProb.get(token));  
		  }
		  return Score;
	  }

}
