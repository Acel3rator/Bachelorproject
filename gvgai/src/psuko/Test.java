package psuko;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class Test {

	public static void main(String[] args) {
		
		final SortedMap<String, Double> map1 = new TreeMap<>();
		
		map1.put("a", 1.0);
		map1.put("c", 0.2);
		map1.put("d", 0.1);
		map1.put("b", 0.24);
		
		final SortedMap<String, Double> map2 = new TreeMap<>();
		map2.putAll(map1);
		
		map1.put("a", 2.0);
		map2.put("a", 3.0);
		
		String s = "( ";
		for (Map.Entry<String, Double> entry : map1.entrySet())
		{
//			entry.setValue(1.0);
			s += ("(" + entry.getKey() + " - " + entry.getValue() + ") ");
		}
		System.out.println(s);
		
		
		
		String t = "( ";
		for (Map.Entry<String, Double> entry : map2.entrySet())
		{
			t += ("(" + entry.getKey() + " - " + entry.getValue() + ") ");
		}
		System.out.println(t);
		
//		List<Double[]> values = new ArrayList<>();
		

		
////		values.add(new Double[]{0.9, 0.9});
////		values.add(new Double[]{1.0, 0.8});
//		
//		values.add(new Double[]{0.5, 0.9});
//		
//		System.out.println(lebesgue2(values));
	}

	
	
    private static double lebesgue2(List<Double[]> values)    //Assumes maximization.
    {
        double dim1 = 0;
        double acum = 0;

        for(int i = 0; i < values.size(); ++i)
        {
            Double[] member = values.get(i);
            double base = member[0] - dim1;
            double height = member[1];
            acum += (base*height);

            dim1 = member[0];
        }

        return acum;
    }
	
	
}
