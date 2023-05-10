package sample;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;

import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;

enum mealsLevel {
    NONE,
    BITE,
    KILL
}

class Rabbit{
    ImageView irabbit;
    Label itext;
    String name;

    int health;
    Rectangle hRect;

    double aimX, aimY;
    final double stepX=10.0;
    final double stepY=10.0;

    public void lifeCycle()
    {
        double x= itext.getLayoutX();
        double y= itext.getLayoutY();

        double dx= aimX-itext.getLayoutX();
        dx=(Math.abs(dx) >stepX)?Math.signum(dx)*stepX:dx;
        double dy= aimY-itext.getLayoutY();
        dy=(Math.abs(dy) >stepY)?Math.signum(dy)*stepY:dy;

        itext.setText(name+" "+x+" "+y);
        itext.setLayoutX(x+dx);
        itext.setLayoutY(y+dy);


        hRect.setX(x+dx);
        hRect.setY(y+dy+18);

        irabbit.setX(x+dx);
        irabbit.setY(y+dy+36);

        if( (Math.abs(aimX-(x+dx)) ==0.0 ) && (Math.abs(aimY-(y+dy)) ==0.0 ))
        {
            aimX= Main.rnd.nextInt((int)Main.scene.getWidth());
            aimY= Main.rnd.nextInt((int)Main.scene.getHeight());
        }

    }

    public Rabbit( Image img, String n, double x, double y, int h )
    {
        name = n;
        health = h;


        itext= new Label(n);
        itext.setFont(new Font(20));

        itext.setText(name+" "+x+" "+y);

        Main.group.getChildren().add(itext);
        itext.setLayoutX(x);
        itext.setLayoutY(y);
        //-----------------------------------------------
        hRect = new Rectangle(health, 10.0, Paint.valueOf("Green"));

        Main.group.getChildren().add(hRect);
        hRect.setX(x);
        hRect.setY(y+18);

        //-----------------------------------------------
        irabbit=new ImageView(img);

        Main.group.getChildren().add(irabbit);

        irabbit.setX(x);
        irabbit.setY(y+36);


        aimX= Main.rnd.nextInt((int)Main.scene.getWidth());
        aimY= Main.rnd.nextInt((int)Main.scene.getHeight());

    }

    public mealsLevel eat( final ReadOnlyObjectProperty<Bounds> wolfBounds )
    {
        if( wolfBounds.get().contains(
                irabbit.getX()+irabbit.boundsInParentProperty().get().getWidth()/2.0,
                irabbit.getY()+irabbit.boundsInParentProperty().get().getHeight()/2.0
        ) )
        {
            //health--;
            decreaseHealth();

            if( health<=0 )
            {
                Main.group.getChildren().remove( irabbit );
                Main.group.getChildren().remove( hRect );
                Main.group.getChildren().remove( itext );
                return mealsLevel.KILL;
            }

            return mealsLevel.BITE;
        }
        else
        {
            return mealsLevel.NONE;
        }
    }

    public void decreaseHealth()
    {
        health-=10;

        if(health>0)
        hRect.setWidth(health);
    }
}

class AdultRabbit extends Rabbit {

    public AdultRabbit(Image img, String n, double x, double y, int h) {
        super(img, n, x, y, h);
    }

    public void decreaseHealth()
    {
        health-=5;
    }

}

class BossRabbit extends AdultRabbit {

    public BossRabbit(Image img, String n, double x, double y, int h) {
        super(img, n, x, y, h);
    }

    public void decreaseHealth()
    {
        health-=1;
    }

}


class Herd {
    ArrayList<Rabbit> herd=new ArrayList<>();

    public Herd()
    {
        Rabbit obama= new Rabbit(Main.imgrabbit, "Obama",
                Main.rnd.nextInt((int)Main.scene.getWidth()),
                Main.rnd.nextInt((int)Main.scene.getHeight()), 75);

        herd.add(obama);

        AdultRabbit trump= new AdultRabbit(Main.imgAdultrabbit, "Trump",
                Main.rnd.nextInt((int)Main.scene.getWidth()),
                Main.rnd.nextInt((int)Main.scene.getHeight()), 75);

        herd.add(trump);

        BossRabbit biden= new BossRabbit(Main.imgBossrabbit, "Biden",
                Main.rnd.nextInt((int)Main.scene.getWidth()),
                Main.rnd.nextInt((int)Main.scene.getHeight()), 75);

        herd.add(biden);


    }

    public void addRabbit(int level, String n, int health )
    {

        Rabbit r=null;
        switch (level)
        {
            case 1:
                r=new AdultRabbit( Main.imgAdultrabbit, n,
                        Main.rnd.nextInt((int)Main.scene.getWidth()),
                        Main.rnd.nextInt((int)Main.scene.getHeight()), health);
                break;
            case 2:
                r=new BossRabbit( Main.imgBossrabbit, n,
                        Main.rnd.nextInt((int)Main.scene.getWidth()),
                        Main.rnd.nextInt((int)Main.scene.getHeight()), health);
                break;
            default:
                r=new Rabbit( Main.imgrabbit, n,
                        Main.rnd.nextInt((int)Main.scene.getWidth()),
                        Main.rnd.nextInt((int)Main.scene.getHeight()), health);
                break;
        }
        herd.add(r);
    }

    public void lifeCycle()
    {
        boolean isAlive=false;
        for( Rabbit r : herd) {
            if (r != null){
                isAlive=true;
                r.lifeCycle();
            }
        }

        if( !isAlive )
            Platform.runLater(()->{
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Всіх зайців з'їли!!!");
                alert.setHeaderText("Ви виграли!!!");

                alert.showAndWait();
                Platform.exit();

            });

    }

    public mealsLevel eat(final ReadOnlyObjectProperty<Bounds> wolfBounds )
    {
        mealsLevel rezult = mealsLevel.NONE;

        for( int i=0; i<herd.size(); i++ )
        {
            if( herd.get(i)!=null )
            {
                mealsLevel r= herd.get(i).eat(wolfBounds);
                if( r == mealsLevel.KILL ) {
                    rezult = mealsLevel.KILL;
                    //herd[i]=null;
                    herd.remove(i);
                    return rezult;
                }
                if( (r == mealsLevel.BITE)  )
                {
                    rezult= mealsLevel.BITE;
                    return rezult;
                }
            }
        }
        return rezult;
    }

}

class Wolf
{
    ImageView imgwolf;
    Label itext;
    String name;

    int daysToLive;
    int daysMax;

    boolean active;

    public void mouseActivate( double mx, double my)
    {
        if( imgwolf.boundsInParentProperty().get().contains(mx,my) ) {

            double x= imgwolf.getX();
            double y= imgwolf.getY();

            Main.group.getChildren().remove(imgwolf);

            active= !active;

            if(active)
            {
                imgwolf=new ImageView(Main.imgwolfActive);
            }
            else
            {
                imgwolf=new ImageView(Main.imgwolf);
            }

            Main.group.getChildren().add(imgwolf);

            imgwolf.setX(x);
            imgwolf.setY(y);

        }

    }

    public Wolf( Image img, String n, double x, double y, int lifeExpectancy )
    {
        name = n;
        imgwolf=new ImageView(img);

        Main.group.getChildren().add(imgwolf);

        imgwolf.setX(x);
        imgwolf.setY(y+22);

        itext= new Label(n);
        itext.setFont(new Font(20));

        itext.setText(name+" "+x+" "+y);
        itext.setLayoutX(x);
        itext.setLayoutY(y);

        Main.group.getChildren().add(itext);

        daysMax= lifeExpectancy;
        daysToLive = lifeExpectancy;

        active= false;
    }

    public void lifeCycle() {
        daysToLive--;

        Main.text.setText(" " + daysToLive);

        if (daysToLive == 0) {
            Main.group.getChildren().remove(itext);
            Main.group.getChildren().remove(imgwolf);
            Main.wolf = null;


            Platform.runLater(()->{
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Вовк здох!!!");
                alert.setHeaderText("Ви програли");

                alert.showAndWait();
                Platform.exit();

            });



            return;
        }

        mealsLevel res = Main.herd.eat(
                imgwolf.boundsInParentProperty());

        if (res == mealsLevel.BITE)daysToLive +=10;


        if (res == mealsLevel.KILL)
             daysToLive = daysMax;
    }

    public boolean isActive(){ return active; }

    public void move( double dx, double dy )
    {
        double x= itext.getLayoutX()+dx;
        double y= itext.getLayoutY()+dy;

        itext.setLayoutX(x);
        itext.setLayoutY(y);

        imgwolf.setX(x);
        imgwolf.setY(y+22);

    }
}

public class Main extends Application {

    public static Image imgrabbit;
    public static Image imgAdultrabbit;
    public static Image imgBossrabbit;
    public static Image imgwolf;
    public static Image imgwolfActive;
    public static ImageView imgviewmap;

    static LocalDateTime beginTime = LocalDateTime.now();
    static int frames=0;
    static Label label= new Label();


    Label itext;
    int counter=0;
    public static Label text;

    boolean gflg=false;

    public static Scene scene;
    public static Random rnd = new Random();

    public static     Herd herd;
    public static Wolf wolf;

    public static Group group;

    @Override
    public void start(Stage primaryStage) throws Exception{
     //   Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        group= new Group();

        //group.getChildren().add(root);

        imgrabbit = new Image("/sample/rabbit.png",100,100,false,false);
        imgAdultrabbit = new Image("/sample/Adultrabbit.png",100,100,false,false);
        imgBossrabbit = new Image("/sample/Bossrabbit.png",100,100,false,false);
        imgwolf = new Image("/sample/wolf.png",100,100,false,false);
        imgwolfActive = new Image("/sample/wolfactive.png",100,100,false,false);

        scene=new Scene(group, 1200, 800);

        text = new Label("TEXT");
        text.setFont(new Font(40));
        group.getChildren().add(text);

        wolf= new Wolf(imgwolf, "Сірий",
                Main.rnd.nextInt((int)Main.scene.getWidth()),
                Main.rnd.nextInt((int)Main.scene.getHeight()), 3600);

        herd=new Herd();

        itext=new Label(" ");
        group.getChildren().add(itext);

        group.getChildren().add(label);
        label.setLayoutX(0);
        label.setLayoutY(50);

        AnimationTimer timer = new AnimationTimer() {

            @Override
            public void handle(long now) {

                if( !gflg ) {
                    System.out.println("AnimationTimer.handle() method thread: "
                            + Thread.currentThread().getId());
                    gflg=true;
                }

                if( wolf!=null )
                    wolf.lifeCycle();

                herd.lifeCycle();

//                ++counter;
//                itext.setText(Integer.toString(counter));

                LocalDateTime nextTime= LocalDateTime.now();
                if( ChronoUnit.SECONDS.between( beginTime,nextTime)>0 )
                {
                    label.setText(Integer.toString(frames));
                    frames=0;
                    beginTime= nextTime;
                }
                else frames++;


                if( imgviewmap!=null )
                {
                    group.getChildren().remove(imgviewmap);
                }
                //saveAsPng( group, "/sample/SnapShot.png");
                final WritableImage SNAPSHOT = group.snapshot(new SnapshotParameters(), null);

                //imgmap = new Image("/sample/SnapShot.png",100,100,false,false);
                imgviewmap=new ImageView(SNAPSHOT);
                group.getChildren().add(imgviewmap);

                //Creating the scale transformation
                Scale scale = new Scale();

                //Setting the dimensions for the transformation
                scale.setX(0.2);
                scale.setY(0.2);

                imgviewmap.setLayoutX(500);
                imgviewmap.getTransforms().add(scale);


            }

        };

        scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                wolf.mouseActivate( event.getX(), event.getY() );
            }
        });

        scene.setOnKeyPressed(new KeyPressedHandler());

        primaryStage.setTitle("Hello World");
        primaryStage.setScene(scene);

        System.out.println("Main.start() method thread: "
                + Thread.currentThread().getId());

        timer.start();
        primaryStage.show();

    }


    private class KeyPressedHandler implements EventHandler<KeyEvent>
    {

        @Override
        public void handle(KeyEvent event) {

            if( wolf==null )return;

            if (event.getCode().equals(KeyCode.INSERT) ) {
                if (event.isControlDown()) {
                    try {
                        DialogNewShip.display();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }


            if( !wolf.isActive() )return;

            if (event.getCode().equals(KeyCode.UP) )
            {
                wolf.move(0,-10);
            }

            if (event.getCode().equals(KeyCode.RIGHT) )
            {
                wolf.move(10,0);
            }

            if (event.getCode().equals(KeyCode.DOWN) )
            {
                wolf.move(0,10);
            }

            if (event.getCode().equals(KeyCode.LEFT) )
            {
                wolf.move(-10,0);
            }

        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}
