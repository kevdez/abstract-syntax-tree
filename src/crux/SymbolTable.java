package crux;
/**
 * studentName = "Kevin Hernandez";
 * studentID = "90872295";
 * uciNetID = "khernan3";
 */


import java.util.LinkedHashMap;

public class SymbolTable {
	
	private SymbolTable parent;
	private SymbolTable child;
	private LinkedHashMap<String,Symbol> map;
	private int depth;
    
    public SymbolTable()
    {
        this.parent = null;   
        this.child = null;
        map = new LinkedHashMap<String,Symbol>();
        
    }
    
    public Symbol lookup(String name) throws SymbolNotFoundError
    {
    	if(map.containsKey(name))
    	{
    		return map.get(name);
    	}
    	else if(this.parent != null)
    	{
    		return this.parent.lookup(name);
    	}
    	else
    		throw new SymbolNotFoundError(name);
    }
       
    
    // MAY HAVE TO LOOK AT THIS LATER
    public Symbol insert(String name) throws RedeclarationError
    {
    	Symbol value = new Symbol(name);
    	if(map.containsKey(name))
    	{
    		throw new RedeclarationError(value);
    	}	
    	else
    	{
    		map.put(name, value);
    		return map.get(name);
    	}
    	
    }
    
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        if (parent != null)
            sb.append(parent.toString());
        
        String indent = new String();
        for (int i = 0; i < depth; i++) {
        	indent += "  ";
        }
        
        for (Symbol s : map.values())
        {
            sb.append(indent + s.toString() + "\n");
        }
        return sb.toString();
    }
    
    public void setParentSymbolTable(SymbolTable par)
    {
    	this.parent = par;
    }
    
    public SymbolTable getParentSymbolTable()
    {
    	return this.parent;
    }
    
    public void setChildSymbolTable(SymbolTable chi)
    {
    	this.child = chi;
    }
    
    public SymbolTable getChildSymbolTable()
    {
    	return this.child;
    }
    
    public void setDepth(int dep)
    {
    	this.depth = dep;
    }
    
    public int getDepth()
    {
    	return this.depth;
    }
}

class SymbolNotFoundError extends Error
{
    private static final long serialVersionUID = 1L;
    private String name;
    
    SymbolNotFoundError(String name)
    {
        this.name = name;
    }
    
    public String name()
    {
        return name;
    }
}

class RedeclarationError extends Error
{
    private static final long serialVersionUID = 1L;

    public RedeclarationError(Symbol sym)
    {
        super("Symbol " + sym + " being redeclared.");
    }
}
