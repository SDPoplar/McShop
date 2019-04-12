package com.seadrip.mc.shop;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.inventory.ItemStack;

import com.earth2me.essentials.api.Economy;
import com.earth2me.essentials.api.NoLoanPermittedException;
import com.earth2me.essentials.api.UserDoesNotExistException;
import com.seadrip.mc.shop.Goods;

public class SdPoplarShop extends JavaPlugin {
    protected List<Goods> m_goods;
    
    @Override
    public void onLoad() {
        this.m_goods = new ArrayList<Goods>();
        this.loadGoodsFromConfigFile();
        this.getLogger().info( "Load successfully" );
    }
    
    @Override
    public void onEnable() {
        
    }
    
    @Override
    public void onDisable() {
        
    }
    
    @Override
    public boolean onCommand( CommandSender sender, Command command, String label, String[] args ) {
        if( !command.isRegistered() ) {
            System.out.println( "Command regist failed" );
            return false;
        }
        
        if( !( sender instanceof Player ) ) {
            System.out.println( "This command must be used by a player" );
            return true;
        }
        
        if( args.length == 0 ) {
            return false;
        }
        
        Player player = ( Player ) sender;
        return this.playerRunCommand( player, args );
    }
    
    protected boolean loadGoodsFromConfigFile() {
        this.saveDefaultConfig();
        FileConfiguration cfg = this.getConfig();
        for( String goodsItem : cfg.getKeys( false ) ) {
            int unitPrice = cfg.getInt( goodsItem );
            Goods g = Goods.CreateGoods( goodsItem, unitPrice );
            if( g != null ) {
                this.m_goods.add( g );
            }
        }
        System.out.println( this.m_goods.size() + " goods loaded successfully" );
        return true;
    }
    
    protected boolean playerRunCommand( Player player, String[] commands ) {
        if( commands[ 0 ].equals( "list" ) ) {
            this.showAllGoodsToPlayer( player );
            return true;
        }
        
        if( commands[ 0 ].equals( "buy" ) ) {
            if( commands.length > 1 ) {
                this.playerBuyGoods( player, commands );
                return true;
            } else {
                return false;
            }
        }
        
        if( commands[ 0 ].equals( "find" ) ) {
            if( commands.length > 1 ) {
                this.playerSearchGoods( player, commands[ 1 ] );
                return true;
            } else {
                return false;
            }
        }
        
        return false;
    }
    
    protected Goods findGoodsByName( String name ) {
        for( Goods g : this.m_goods ) {
            if( g.getDisplayName().equals( name ) ) {
                return g;
            }
        }
        return null;
    }
    
    protected void playerSearchGoods( Player player, String parten ) {
        int nFound = 0;
        for( Goods g : this.m_goods ) {
            if( g.getDisplayName().indexOf( parten ) == -1 ) {
                continue;
            }
            
            player.sendMessage( g.toString() );
            nFound++;
        }
        if( nFound == 0 ) {
            player.sendMessage( "No goods found" );
        }
    }
    
    protected void playerBuyGoods( Player player, String[] commands ) {
        Goods g = this.findGoodsByName( commands[ 1 ] );
        if( g == null ) {
            player.sendMessage( "Goods not found" );
            return;
        }
        
        int amount = commands.length > 2 ? Integer.parseInt( commands[ 2 ] ) : 1;
        int cost = g.getCost( amount );
        //  check balance
        String playerName = player.getName();
        if( playerName == null ) {
            System.out.println( "Player name is null, display name: " + player.getDisplayName() );
        }
        BigDecimal decCost = new BigDecimal( cost );
        try {
            if( !Economy.hasEnough( playerName, decCost ) ) {
                player.sendMessage( "You donot have enough balance" );
                return;
            }
            Economy.add( playerName, decCost.divide( new BigDecimal( -1 ) ) );
            player.sendMessage( "You bought " + g.getDisplayName() + " x " + amount + ", $" + cost + " cost" );
            player.sendMessage( "Current Balance: $" + Economy.getMoneyExact( playerName ).toString() );
            System.out.println( "Player " + player.getName() + " cost $" + cost + " to buy "
                    + g.getDisplayName() + " x " + amount );
            ItemStack item = new ItemStack( g.getMaterial(), amount );
            player.getInventory().addItem( item );
        } catch (ArithmeticException e) {
        } catch (UserDoesNotExistException e) {
            player.sendMessage( "You have no bank account, call server admin for help" );
        } catch (NoLoanPermittedException e) {
            player.sendMessage( "No loan permitted" );
        }
    }
    
    protected void showAllGoodsToPlayer( Player player ) {
        player.sendMessage( "Welcome to this Shop" );
        if( this.m_goods.size() == 0 ) {
            player.sendMessage( "But we don't have any goods yet" );
            return;
        }
        for( Goods g : this.m_goods ) {
            player.sendMessage( g.toString() );
        }        
    }
}
