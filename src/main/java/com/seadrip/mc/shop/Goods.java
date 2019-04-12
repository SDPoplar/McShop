package com.seadrip.mc.shop;

import org.bukkit.Material;

public class Goods {
    protected String m_sDisplayName;
    protected Material m_material;
    protected int m_nUnitPrice;
    
    //  protected static 
    
    public static Goods CreateGoods( String name, int unitPrice )
    {
        Material type = Material.getMaterial( name.toUpperCase() );            
        return ( type != null ) ? new Goods( name, type, unitPrice ) : null;
    }

    protected Goods( String displayName, Material type, int unitPrice ) {
        this.m_sDisplayName = displayName;
        this.m_material = type;
        this.m_nUnitPrice = unitPrice;
    }
    
    @Override
    public String toString() {
        return this.m_sDisplayName + " - $" + this.m_nUnitPrice;
    }
    
    public String getDisplayName() {
        return this.m_sDisplayName;
    }
    
    public Material getMaterial() {
        return this.m_material;
    }
    
    public int getUnitPrice() {
        return this.m_nUnitPrice;
    }
    
    public int getCost( int amount ) {
        return this.getUnitPrice() * amount;
    }
}
