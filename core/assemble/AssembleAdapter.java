package com.sweatsunited.core.assemble;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

public interface AssembleAdapter {

	/**
	 * Get's the scoreboard title.
	 *
	 * @param player who's title is being displayed.
	 * @return title.
	 */
	String getTitle(Player player);

	/**
	 * Get's the scoreboard lines.
	 *
	 * @param player who's lines are being displayed.
	 * @return lines.
	 */
	List<String> getLines(Player player);


	LinkedHashMap<UUID,List<String>> getArrayCache();

}
