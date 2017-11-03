package fr.soreth.VanillaPlus.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;


@SuppressWarnings("rawtypes")
public class WeightedRandom {
	public static int totalWeight(Collection<? extends WeightedRandom.Choice> collection) {
		int i = 0;
        WeightedRandom.Choice weightedrandomChoice;
        for (Iterator iterator = collection.iterator(); iterator.hasNext(); i += weightedrandomChoice.weight) {
        	weightedrandomChoice = (WeightedRandom.Choice) iterator.next();
        }
        return i;
	}
	public static <T extends WeightedRandom.Choice> T getChoice(Random random, Collection<T> collection) {
        return getChoice(random, collection, totalWeight(collection));
    }
	public static <T extends WeightedRandom.Choice> T getChoice(Random random, Collection<T> collection, int i) {
    	if (i <= 0) {
        	i=1;
        }
        int j = random.nextInt(i);
        return getChoice(collection, j);
    }

	public static <T extends WeightedRandom.Choice> T getChoice(Collection<T> collection, int i) {
		Iterator<T> iterator = collection.iterator();
		T weightedrandomChoice;
		do {
			if (!iterator.hasNext()) {
				return null;
			}
			weightedrandomChoice = iterator.next();
			i -= weightedrandomChoice.weight;
		} while (i >= 0);
		return weightedrandomChoice;
	}

    public static class Choice {

        protected int weight;

        public Choice(int weight) {
            this.weight = weight;
        }
    }
	public static <T extends WeightedRandom.RestrictedChoice> List<T> getRestrictedChoice(Random random,
				HashMap<? extends Collection<T>, LiteEntry<Integer, Integer>> collection, HashMap<Integer, Integer> restriction) {
		List<T>result = new ArrayList<>();
		for(Entry<? extends Collection<T>, LiteEntry<Integer, Integer>>entry : collection.entrySet()) {
			result.addAll(getChoices(random, entry.getKey(), entry.getValue().getKey(), entry.getValue().getValue(), restriction));
		}
        return result;
    }
	private static <T extends WeightedRandom.RestrictedChoice> List<T> getChoices(Random random, Collection<T> collection, Integer min, Integer max,
			HashMap<Integer, Integer> restriction) {
		if( min < 0)	min = -min;
		if(max < min)	max = min;
		int amount = ( max > min ) ? ( random.nextInt(max - min + 1 ) + min ) : max;
		List<T>result = new ArrayList<>();
		List<T>copy = new ArrayList<>(collection);
		while(amount != 0 && !copy.isEmpty()) {
			T value = getChoice(random, copy);
			Integer i = restriction.get(value.restrictionId);
			if(i == null) {
				result.add(value);
				amount --;
			}else if(i > 0) {
				result.add(value);
				restriction.put(value.restrictionId, i - 1);
				amount --;
			} else {
				copy.remove(value);
			}
		}
		return result;
	}
    public static class RestrictedChoice extends Choice{

        protected int restrictionId;

        public RestrictedChoice(int weight, int restrictionId) {
        	super(weight);
        	this.restrictionId = restrictionId;
		}
    }
}