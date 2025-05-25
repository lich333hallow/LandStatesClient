//package ru.lich333hallow.LandStates.components;
//
//import com.badlogic.gdx.graphics.Texture;
//import com.badlogic.gdx.graphics.g2d.TextureRegion;
//import com.badlogic.gdx.scenes.scene2d.InputEvent;
//import com.badlogic.gdx.scenes.scene2d.ui.Image;
//import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
//import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
//
//import ru.lich333hallow.LandStates.Main;
//import ru.lich333hallow.LandStates.models.PlayerInGame;
//
//public class BaseClickListener extends ClickListener {
//    private final Image base;
//    private final Texture miner;
//    private final Texture defender;
//    private final PlayerInGame player;
//
//    private final Main main;
//
//    private boolean isSelected = true;
//
//    public BaseClickListener(Main main, Image base, Texture miner, Texture defender, PlayerInGame player) {
//        this.main = main;
//        this.base = base;
//        this.miner = miner;
//        this.defender = defender;
//        this.player = player;
//    }
//
//    @Override
//    public void clicked(InputEvent event, float x, float y) {
//        if(player.getBalance() - Main.cost < 0 ) return;
//        if(isSelected){
//            player.setBalance(player.getBalance() - Main.cost);
//            base.setDrawable(new TextureRegionDrawable(new TextureRegion(miner)));
//            main.getGameScreen().updateCoins(player);
//        } else {
//            player.setBalance(player.getBalance() - Main.cost);
//            base.setDrawable(new TextureRegionDrawable(new TextureRegion(defender)));
//            main.getGameScreen().updateCoins(player);
//        }
//        isSelected = !isSelected;
//    }
//}
