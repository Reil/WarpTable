import java.util.ArrayList;
import java.util.logging.Logger;

public class WarpTable extends Plugin {
	protected static final int lineLength = 312;
	protected static final int entryLength = 78;
	protected static final int entriesPerPage = 28;
	
    private WarpTableListener listener = new WarpTableListener();

    protected static final Logger log = Logger.getLogger("Minecraft");
    // private final String newLine = System.getProperty("line.separator");
    protected String warpFile;
    
    public WarpTable() {
	}
	
    public void initialize()
    {
        etc.getLoader().addListener(PluginLoader.Hook.COMMAND, listener, this, PluginListener.Priority.MEDIUM);
    }
	
	public boolean load() {
        return true;	
    }
    
    public void enable() {
        if (load())
		{
            log.info("[WarpTable] Enabled.");
			etc.getInstance().addCommand("/warptable",   "<pagenumber> - Nicer formatted table of warps.");
			etc.getInstance().addCommand("/plugintable", "<pagenumber> - Nicer formatted table of plugins.");
		}	
        else
		{
            log.info("[WarpTable] Error while loading.");
        }
    }
    

    public void disable() {
		etc.getInstance().removeCommand("/warptable");
        log.info("[WarpTable] Mod Disabled.");
		
    }
    /* Being borrowed/adapted code from vMinecraft by nossr50 */
	 public static int msgLength(String str){
			int length = 0;
			//Loop through all the characters, skipping any color characters
			//and their following color codes
			for(int x = 0; x<str.length(); x++)
			{
				int len = charLength(str.charAt(x));
				length += len;
			}
			return length;
    }
	 
	 private static int charLength(char x)
	{
	    	if("i.:,;|!".indexOf(x) != -1)
				return 2;
			else if("l'".indexOf(x) != -1)
				return 3;
			else if("tI[]".indexOf(x) != -1)
				return 4;
			else if("fk{}<>\"*()".indexOf(x) != -1)
				return 5;
			else if("abcdeghjmnopqrsuvwxyzABCDEFGHJKLMNOPQRSTUVWXYZ1234567890\\/#?$%-=_+&^".indexOf(x) != -1)
				return 6;
			else if("@~".indexOf(x) != -1)
				return 7;
			else if(x==' ')
				return 4;
			else
				return -1;
    }
	/* End code borrowed from nossr50's vMinecraft*/
	 
	public String[] makeTable(String spaceSeperated, int pageNumber){
		ArrayList <String> messages = new ArrayList<String>();
		String output = new String();
        String [] warpListSplit = spaceSeperated.split("\\s+");
        int numPages = warpListSplit.length / entriesPerPage;
        if (warpListSplit.length % entriesPerPage >= 1) numPages ++;
        if (pageNumber  > numPages) {
        	messages.add("Invalid page number!");
        	return messages.toArray(new String[messages.size()]);
        }
        /* Display page # */
    	messages.add("Page #" + pageNumber + " of " + numPages);
        /* Display stuff */
        for (int i = (pageNumber-1) * entriesPerPage ; i < (pageNumber * entriesPerPage) && i < warpListSplit.length; i++) {
        	String warpItem = warpListSplit[i];
        	if (msgLength(output) + msgLength (warpItem) > lineLength ) {
        		messages.add(new String(output));
        		output = new String();
        	}
    		output += warpItem + " ";
    		if (lineLength - msgLength(output) < entryLength){
        		messages.add(new String (output));
        		output = new String();
        	} else for (int Compensating = entryLength - (msgLength(output) % entryLength); Compensating > 1;)
    		{
    			switch (Compensating % 4) {
    				case 0:
    					output += " ";
    					Compensating -= 4;
    					break;
    				case 2:
    					output += ".";
    					Compensating -=2;
    					break;
    				case 1:
    				case 3:
    					output += "'";
    					Compensating -=3;
    					break;
    			}
    			
    		}
        }
        return messages.toArray(new String[messages.size()]);
	}
    public class WarpTableListener extends PluginListener
    {

	    public boolean onCommand(Player player, String[] split) {
	        if (!player.canUseCommand(split[0]))
	            return false;
	        
	        if (split[0].equalsIgnoreCase("/warptable")) {
	        	int pageNumber;
	        	if (split.length == 1)
	        		pageNumber = 1;
	        	else try {
	        		pageNumber = new Integer(split[1]);
	        	} catch (NumberFormatException ex){
	        		player.sendMessage("Invalid page number!");
	        		return true;
	        	}
	        	String warpList = etc.getDataSource().getWarpNames(player);
	            for (String output : makeTable(warpList, pageNumber))
	            player.sendMessage(output);
	            return true;
	        } else if (split[0].equalsIgnoreCase("/plugintable")){
	        	int pageNumber;
	        	if (split.length == 1)
	        		pageNumber = 1;
	        	else try {
	        		pageNumber = new Integer(split[1]);
	        	} catch (NumberFormatException ex){
	        		player.sendMessage("Invalid page number!");
	        		return true;
	        	}
	        	String warpList = etc.getLoader().getPluginList();
	            for (String output : makeTable(warpList, pageNumber))
	            player.sendMessage(output);
	            return true;
	        }
	        else
	            return false;
	    }
    }
}