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
import edu.asu.sml.PorterStemmer;;


public class Logistic_Regress 
{
	static PorterStemmer ps = new PorterStemmer();
	final double lrate = 0.1;
	final int iterartions = 10;
    public static void main( String[] args )
    {    
    	
    	Logistic_Regress lr = new Logistic_Regress();
    	DiabetesSisters diabetesSisters = DiabetesSisters.getInstance();
	    Map<String,List<String>> trainingData =diabetesSisters.getTrainingData();
	    Map<String,List<String>> testData = diabetesSisters.getTestData();
	    Map<String,List<String>> completeData = diabetesSisters.getCompleteData();
	    
	    
	    
	    
	    
	    Set<String> V = lr.getVocabulary(completeData);
	    
	    
	    HashMap<String, List<Set<String>>> featuresc = lr.getFeatures(V, trainingData);
	    HashMap<String, List<HashMap<String,Double>>> features = lr.getFeaturecount( featuresc , trainingData);
	    HashMap<String , HashMap<String , Double>> thetas= lr.setthetas(V, features);
	    HashMap<String , HashMap<String , Double>> newthetas = lr.computethetas(thetas, V, features);
	    Map<String , Integer> Classvalues = new HashMap<String, Integer>();
	    String[] name = new String[4];
	    int j = 0;  
	    for(String key: trainingData.keySet())
		  {
			name[j] = key;  
			j++;
		  }
	    Classvalues.put(name[0],1);
	    Classvalues.put(name[1],2);
	    Classvalues.put(name[2],3);
	    Classvalues.put(name[3],4);
	    
	    int count = 0;
	    int tot =0;
	    
	    for(String trueclass:testData.keySet())
	    {
	    	List<String> instances = testData.get(trueclass);
	    	for(String instance:instances)
	    	{
	    		Set<String> testfeatures = new HashSet<String>();
	    		String[] word = instance.split(" ");
	    		for(int i=0 ; i<word.length; i++)
	    		{
	    			word[i] = ps.stemString(word[i]);
	    			if(V.contains(word[i]))
	    			{
	    				testfeatures.add(word[i]);
	    			}
	    		}
	    		tot++;
	    		int out = lr.predictclass(testfeatures , thetas);
	    		if(Classvalues.get(trueclass) == out)
	    			count++;
	    				
	    	}
	    }
	    System.out.println("A total of "+ count+"correct results of "+tot+"number of given data");
    }
    
   
    
   public HashMap<String, List<HashMap<String,Double>>> getFeaturecount(HashMap<String, List<Set<String>>> features , Map<String,List<String>> trainingData)
    {
    	HashMap<String, List<HashMap<String,Double>>> featurecountc = new HashMap<String, List<HashMap<String,Double>>>();
    	for(String key:trainingData.keySet())
    	{
    		List<HashMap<String,Double>> feat = new ArrayList<HashMap<String,Double>>();
    		List<String> instances = trainingData.get(key);
    		List<Set<String>> instancefeatures = features.get(key);
    		int index = 0;
    		for(String instance:instances)
    		{
    			HashMap<String , Double> fet = new HashMap<String , Double>();
    			String[] words = instance.split(" ");
    			for(int i=0; i<words.length;i++)
    			{
    			words[i] = ps.stemString(words[i]);
    				if(instancefeatures.get(index).contains(words[i]))
    				{
    					if(fet.containsKey(words[i]))
    					{
    					double val = fet.get(words[i]) +1;
    					fet.put(words[i] , val);
    					}
    					else
    					fet.put(words[i] , 1.00);
    				}
    					
    			}
    			feat.add(fet);
    			index++;
    		}
    		featurecountc.put(key, feat);
    	}
    	return featurecountc;
    }

    
    public Set<String> getVocabulary(Map<String, List<String>> completeData  )
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
					  eachword[i] = ps.stemString(eachword[i]);
					  if(!eachword.equals("\t"))
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
		        System.out.println("Couldn't find the  Directory.");
		        e.printStackTrace();
		      }catch (Exception e){
		        e.printStackTrace();
		      }
		 System.out.println("Vocab size"+Vocab.size());
		  return Vocab;
		  
		  
	  }
    
    public HashMap<String, List<Set<String>>> getFeatures(Set<String> V , Map<String, List<String>> trainingData)
    {
    	HashMap<String , List<Set<String>>> featurevectors = new HashMap<String , List<Set<String>>>(); 
    	for(String key:trainingData.keySet())
    	{
    		List<Set<String>> Xsofaclass = new ArrayList<Set<String>>();
    		List<String> instances = trainingData.get(key);
    		for(String instance:instances)
    		{
    			Set<String > wordvector = new HashSet<String>();
    			String[] words = instance.split(" ");
    			for(int i=0 ; i<words.length ; i++)
    			{
    				words[i] = ps.stemString(words[i]);
    			if(!wordvector.contains(words[i]) && V.contains(words[i]))
    			{
    				wordvector.add(words[i]);
    			}
    			}
    			Xsofaclass.add(wordvector); 
    		}
    		 featurevectors.put(key, Xsofaclass);  		
    	}
    	
    	return featurevectors;
    }
    public HashMap<String , HashMap<String , Double>> setthetas(Set<String> V,HashMap<String, List<HashMap<String,Double>>> features)
    {
    	HashMap<String , HashMap<String , Double>> thetas = new HashMap<String , HashMap<String , Double>>();
    	Logistic_Regress lr = new Logistic_Regress();
    	//initialize all theta values to zero
    	for(String key:features.keySet())
    	{
    		HashMap<String , Double> t = new HashMap<String , Double>();
    		for(String eachword:V)
    		{
    			t.put(eachword,0.00);
    		}
    		thetas.put(key, t);
    	}
    	return thetas;
    }
    
    public HashMap<String , HashMap<String , Double>> computethetas(HashMap<String, HashMap<String, Double>> thetas,Set<String> V,HashMap<String, List<HashMap<String,Double>>> features)
    {
    	
    	Logistic_Regress lr = new Logistic_Regress();
    	double m = 0.00;
    	for(String classname:features.keySet())
    	{
    		List<HashMap<String , Double>> allfeatures = features.get(classname);
    		m += allfeatures.size();
    	}
    	System.out.println("total instances:"+m);
    	//iteratively calculate theta values and update thetas.
    	for(int i=0 ; i< iterartions ; i++)
    	{
    		System.out.println(i);
    		for(String key: thetas.keySet())
    		{
    			System.out.println(" thetas class:"+key);
    			HashMap<String , Double> theta = thetas.get(key);
    			//System.out.println("features of each theta:"+theta.size());
    			int xyz = 1;
    			for(String word:theta.keySet())
    			{
    				System.out.print("-"+xyz+" - ");
    			Double thetavalue = theta.get(word);
    			System.out.print(word+"-"+theta.get(word)+" - ");
    			thetavalue =thetavalue+ lrate*lr.calculatecost(key,word ,features, V,thetas)/m;
    			
    			theta.put(word, thetavalue);
    			System.out.println(theta.get(word));
    			xyz++;
    			}
    			
    			thetas.put(key, theta);
    			System.out.println("theta value is updated");
    		}
    	}
    	return thetas;
    }
    
    
    public double calculatecost(String currentclass,String word ,HashMap<String, List<HashMap<String,Double>>> features ,Set<String> V,HashMap<String, HashMap<String, Double>> thetas)
    {
    	Logistic_Regress lr = new Logistic_Regress();
    	double cost = 0.00;
    	for(String key:features.keySet())
    	{
    		if(currentclass.equals(key))
    		{
    			List<HashMap<String , Double>> currentfeatures = features.get(key);
    			//System.out.println("features considered"+currentfeatures.size());
    			for(HashMap<String , Double> eachfeature:currentfeatures)
    			{
    			if(eachfeature.containsKey(word))
    			{
    				cost+= 1 - lr.calculateprobabilities(eachfeature , V,thetas,word,currentclass);	
    			}
    				
    			}
    		}
    		else
    		{
    			List<HashMap<String , Double>> otherfeatures = features.get(key);
    			//System.out.println("features not considered"+otherfeatures.size());
    			for(HashMap<String , Double> eachfeature:otherfeatures)
    			{
    				if(eachfeature.containsKey(word))
    				cost+=-lr.calculateprobabilities(eachfeature , V , thetas,word,currentclass);
    			}
    		}
    	}
    	return cost;
    }
    //V is not required
    public double calculateprobabilities(HashMap<String , Double> eachfeatures,Set<String> V,HashMap<String, HashMap<String, Double>> thetas ,String word,String currentclass)
    {
    	//calculate probabilities for all the currentfeatures.HAndle theta0 case
    	HashMap<String , Double> curtheta = thetas.get(currentclass);
    	double num = 1;
    	double tot=0.00;
    	//System.out.println("total words"+curtheta.size());
    	for(String eachword:curtheta.keySet())
    	{
    		if(eachfeatures.containsKey(eachword))
    			num+=curtheta.get(eachword)*eachfeatures.get(eachword);
    	}
    	
    	for(String classn : thetas.keySet())
    	{
    		HashMap<String , Double> theta = thetas.get(classn);
    		double val = 1;
    		for(String eachword:theta.keySet())
        	{
        		if(eachfeatures.containsKey(eachword))
        			val+=curtheta.get(eachword)*eachfeatures.get(eachword);
        	}
    		tot+=Math.exp(val);
    	}
    	//System.out.println("prob"+Math.exp(num)/tot);
    	return Math.exp(num)/tot;
    }
    
 
    public int predictclass(Set<String>testfeatures , HashMap<String, HashMap<String, Double>> thetas)
    {
    	Logistic_Regress lr = new Logistic_Regress();
    	double tot=0.00;
    	double num = 0.00;
    	int estclass;
    	double[] prob = new double[4];
    	for(String classn : thetas.keySet())
    	{
    		HashMap<String , Double> theta = thetas.get(classn);
    		double val = 1;
    		for(String eachword:theta.keySet())
        	{
        		if(testfeatures.contains(eachword))
        			val+=theta.get(eachword);
        	}
    		tot+=Math.exp(val);
    	}
    	int j=0;
    	for(String classn : thetas.keySet())
    	{
    		HashMap<String , Double> theta = thetas.get(classn);
    		for(String eachword:theta.keySet())
        	{
        		if(testfeatures.contains(eachword))
        			num+=theta.get(eachword);
        	}
    		prob[j] = Math.exp(num)/tot;
    		j++;
       	}
    	
    	estclass = lr.maxim(prob);
    	
    	return estclass;
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
    
}
