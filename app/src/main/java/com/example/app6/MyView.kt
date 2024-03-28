package com.example.app6

import android.content.Context
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.TextView

class MyView : View
{

    private var selectedItem : ImageView? = null
    private var enemySpawned : ImageView? = null

    private var startgame : Boolean = false
    private var controlstart : Boolean = false
    var in_arena : Boolean = false
    var lion_touched : Boolean = false
    var rabbit_touched : Boolean = false
    var cobra_touched : Boolean = false
    var finger_up : Boolean = false
    var chosenFlipped : Boolean = false
    var enemyFlipped : Boolean = false

    private var playerscore = 0
    private var computerscore = 0
    var num : Int = 0

    private var arenaRect = Rect(0,0,0,0)
    private var rabbitRect = Rect(0,0,0,0)
    private var lionRect = Rect(0,0,0,0)
    private var cobraRect = Rect(0,0,0,0)
    private var chosenRect = Rect(0,0,0,0)
    private var enemyRect = Rect(0,0,0,0)


    //records the first point
    var startPointLion : Point = Point(0,0)
    var offsetLion : Point = Point(0,0)
    var offsetRabbit : Point = Point(0,0)
    var startPointRabbit : Point = Point(0,0)
    var offsetCobra : Point = Point(0,0)
    var startPointCobra : Point = Point(0,0)

    var lionOrigin = Rect(0,0,0,0)
    var rabbitOrigin = Rect(0,0,0,0)
    var cobraOrigin = Rect(0,0,0,0)

    var leoLeftOrigin = 0
    var leoTopOrigin = 0
    var rabLeftOrigin = 0
    var rabTopOrigin = 0
    var cobLeftOrigin = 0
    var cobTopOrigin = 0

    companion object {
        private var instance: MyView? = null
        public fun getInstance(): MyView {
            return instance!!
        }
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.setWillNotDraw(false)
        instance = this

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        var title1 : ImageView? = MainActivity.getInstance().findViewById(R.id.title1)
        var arena = MainActivity.getInstance().findViewById<ImageView>(R.id.arena)
        var lion = MainActivity.getInstance().findViewById<ImageView>(R.id.lion)
        var rabbit = MainActivity.getInstance().findViewById<ImageView>(R.id.rabbit)
        var cobra = MainActivity.getInstance().findViewById<ImageView>(R.id.cobra)
        var chosen = MainActivity.getInstance().findViewById<ImageView>(R.id.chosen)
        var enemy = MainActivity.getInstance().findViewById<ImageView>(R.id.enemy)

        arenaRect = Rect(arena.left,arena.top,arena.right,arena.bottom)
        //arenaRect = Rect(arena.x as Int,arena.y as Int,arena.x as Int + ,arena.bottom)
        rabbitRect = Rect(rabbit.left,rabbit.top,rabbit.right,rabbit.bottom)
        rabbitOrigin = Rect(rabbit.left,rabbit.top,rabbit.right,rabbit.bottom)
        lionRect = Rect(lion.left,lion.top,lion.right,lion.bottom)
        lionOrigin = Rect(lion.left,lion.top,lion.right,lion.bottom)
        cobraRect = Rect(cobra.left,cobra.top,cobra.right,cobra.bottom)
        cobraOrigin = Rect(cobra.left,cobra.top,cobra.right,cobra.bottom)
        chosenRect = Rect(chosen.left,chosen.top,chosen.right,chosen.bottom)
        enemyRect = Rect(enemy.left,enemy.top,enemy.right,enemy.bottom)

        leoLeftOrigin = lion.left
        leoTopOrigin = lion.top
        rabLeftOrigin = rabbit.left
        rabTopOrigin = rabbit.top
        cobLeftOrigin = cobra.left
        cobTopOrigin = cobra.top

        //Title animation
        var myZoom = AnimationUtils.loadAnimation(MainActivity.getInstance(),R.anim.zoom_in)
        var introHandler = Intro()
        myZoom.setAnimationListener(introHandler)
        title1?.startAnimation(myZoom)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        var delta_x : Float
        var delta_y : Float
//printing out to the console for debugging purposes
        println("Current coordinates:\n" +
                "${lionRect.left}" + " ${lionRect.top}" + " ${lionRect.right}" + " ${lionRect.bottom}\n" +
                "${rabbitRect.left}" + " ${rabbitRect.top}" + " ${rabbitRect.right}" + " ${rabbitRect.bottom}\n" +
                "${cobraRect.left}" + " ${cobraRect.top}" + " ${cobraRect.right}" + " ${cobraRect.bottom}")

        //boundary check
        var lion = MainActivity.getInstance().findViewById<ImageView>(R.id.lion)
        var rabbit = MainActivity.getInstance().findViewById<ImageView>(R.id.rabbit)
        var cobra = MainActivity.getInstance().findViewById<ImageView>(R.id.cobra)
        var chosen = MainActivity.getInstance().findViewById<ImageView>(R.id.chosen)
        var enemy = MainActivity.getInstance().findViewById<ImageView>(R.id.enemy)
        var arenaRect = MainActivity.getInstance().findViewById<ImageView>(R.id.arena)

//when finger is triggered, it will check if the item is inside the arena rect
        if (finger_up) {
            if ((lion.left > arenaRect.left) && (lion.top > arenaRect.top) &&
                (lion.left < arenaRect.right) && (lion.top < arenaRect.bottom)) {
                startgame = true
                in_arena = true
                chosen.tag = "Lion"
                lionStart()
            }
            else if ((rabbit.left > arenaRect.left) && (rabbit.top > arenaRect.top) &&
                (rabbit.left < arenaRect.right) && (rabbit.top < arenaRect.bottom)) {
                startgame = true
                in_arena = true
                chosen.tag = "Rabbit"
                rabbitStart()
            }
            else if ((cobra.left > arenaRect.left) && (cobra.top > arenaRect.top) &&
                (cobra.left < arenaRect.right) && (cobra.top < arenaRect.bottom)) {
                startgame = true
                in_arena = true
                chosen.tag = "Cobra"
                cobraStart()
            }
            else
                in_arena = false
        }

/*********************************************************************************************/
        if (startgame) {
            //choose enemy
            when ((0..2).random()) {
                0 -> {
                    enemy.setImageResource(R.drawable.lion0)
                    enemy.tag = "Lion"
                    enemy.scaleX = -1.0f
                }
                1 -> {
                    enemy.setImageResource(R.drawable.rabbit)
                    enemy.tag = "Rabbit"
                }
                else -> {
                    enemy.setImageResource(R.drawable.cobra)
                    enemy.tag = "Cobra"
                    enemy.scaleY = -1.0f
                }
            }

            startingGame()


            chosen.scaleX = 1.0f
            enemy.scaleX = 1.0f
            startgame = false
            in_arena = false
        }

        /*********************************************************************************************/

        if (controlstart) {
            if(lion_touched) {
                lion.setX(lionRect.left.toFloat())
                lion.setY(lionRect.top.toFloat())
                lion.left = lionRect.left
                lion.top = lionRect.top
                lion.right = lionRect.right
                lion.bottom = lionRect.bottom
            }
            if (cobra_touched) {
                cobra.setX(cobraRect.left.toFloat())
                cobra.setY(cobraRect.top.toFloat())
                cobra.left = cobraRect.left
                cobra.top = cobraRect.top
                cobra.right = cobraRect.right
                cobra.bottom = cobraRect.bottom
            }
            if (rabbit_touched) {
                rabbit.setX(rabbitRect.left.toFloat())
                rabbit.setY(rabbitRect.top.toFloat())
                rabbit.left = rabbitRect.left
                rabbit.top = rabbitRect.top
                rabbit.right = rabbitRect.right
                rabbit.bottom = rabbitRect.bottom
            }
        }
        /*********************************************************************************************/

        if (finger_up)
            finger_up = false

    }
    /*********************************************************************************************/
    private fun startingGame() {
        var enemy = MainActivity.getInstance().findViewById<ImageView>(R.id.enemy)
        val myFadeIn = AnimationUtils.loadAnimation(MainActivity.getInstance(), R.anim.fade_in)
        val animHandler1 = FadeInHandler2()
        myFadeIn.setAnimationListener(animHandler1)
        enemy.startAnimation(myFadeIn)
    }
    /*********************************************************************************************/

    //Handlers for Touch Events and Animations
    //Touch Handling
    override fun onTouchEvent(event: MotionEvent): Boolean {
        var lion = MainActivity.getInstance().findViewById<ImageView>(R.id.lion)
        var rabbit = MainActivity.getInstance().findViewById<ImageView>(R.id.rabbit)
        var cobra = MainActivity.getInstance().findViewById<ImageView>(R.id.cobra)
        var x = event.getX()
        var y = event.getY()

        /*********************************************************************************************/
        if (event.getAction() == MotionEvent.ACTION_DOWN )
        {
            if (startgame)
                return false

            controlstart = true

            if ((x > lion.left) && (x < lion.right) &&
                (y > lion.top) && (y < lion.bottom))
            {
                lionRect = Rect(lion.left,lion.top,lion.right,lion.bottom)
                lion_touched = true
                startPointLion.set(x.toInt(),y.toInt())
                offsetLion.set((x - lionRect.left).toInt(), (y - lionRect.top).toInt())
                //lion.startAnimation(LionAnim)
            }
            else if ((x > rabbit.left) && (x < rabbit.right) &&
                (y > rabbit.top) && (y < rabbit.bottom)) {

                rabbitRect = Rect(rabbit.left,rabbit.top,rabbit.right,rabbit.bottom)
                rabbit_touched = true
                println("touched rabbit")

                startPointRabbit.set(x.toInt(),y.toInt())
                offsetLion.set((x - rabbitRect.left).toInt(), (y - rabbitRect.top).toInt())
            }
            else if ((x > cobra.left) && (x < cobra.right) &&
                (y > cobra.top) && (y < cobra.bottom)) {

                cobraRect = Rect(cobra.left,cobra.top,cobra.right,cobra.bottom)
                cobra_touched = true
                cobra_touched = true
                println("touched cobra")

                startPointCobra.set(x.toInt(),y.toInt())
                offsetLion.set((x - cobraRect.left).toInt(), (y - cobraRect.top).toInt())

            }
        }
        else if (event.getAction() ==  MotionEvent.ACTION_MOVE) {
            println("attemped move")
            if (lion_touched)
            {
                lionRect.left = x.toInt() - offsetLion.x
                lionRect.top = y.toInt() - offsetLion.y
                lionRect.right = (x + lion.getWidth() - offsetLion.x).toInt()
                lionRect.bottom = (y + lion.getHeight() - offsetLion.y).toInt()
                println("attemped move for lion")
                lion.setX(lionRect.left.toFloat())
                lion.setY(lionRect.top.toFloat())
                //trigger redraw
                this.invalidate()

            } else if (rabbit_touched) {

                rabbitRect.left = x.toInt() - offsetRabbit.x
                rabbitRect.top = y.toInt() - offsetRabbit.y
                rabbitRect.right = (x + rabbit.getWidth() - offsetRabbit.x).toInt()
                rabbitRect.bottom = (y + rabbit.getHeight() - offsetRabbit.y).toInt()
                println("attemped move for rabbit")
                rabbit.setX(rabbitRect.left.toFloat())
                rabbit.setY(rabbitRect.top.toFloat())
                this.invalidate()

            } else if (cobra_touched) {

                cobraRect.left = x.toInt() - offsetCobra.x
                cobraRect.top = y.toInt() - offsetCobra.y
                cobraRect.right = (x + cobra.getWidth() - offsetCobra.x).toInt()
                cobraRect.bottom = (y + cobra.getHeight() - offsetCobra.y).toInt()
                println("attemped move for cobra")
                cobra.setX(cobraRect.left.toFloat())
                cobra.setY(cobraRect.top.toFloat())
                this.invalidate()
            }
        }
        else if (event.getAction() == MotionEvent.ACTION_UP) {
            finger_up = true
            if (lion_touched)
            {
                lionRect.left = x.toInt() - offsetLion.x
                lionRect.top = y.toInt() - offsetLion.y
                lionRect.right = (x + lion.getWidth() - offsetLion.x).toInt()
                lionRect.bottom = (y + lion.getHeight() - offsetLion.y).toInt()
                this.invalidate()

            } else if (rabbit_touched) {

                rabbitRect.left = x.toInt() - offsetRabbit.x
                rabbitRect.top = y.toInt() - offsetRabbit.y
                rabbitRect.right = (x + rabbit.getWidth() - offsetRabbit.x).toInt()
                rabbitRect.bottom = (y + rabbit.getHeight() - offsetRabbit.y).toInt()
                this.invalidate()

            } else if (cobra_touched) {

                cobraRect.left = x.toInt() - offsetCobra.x
                cobraRect.top = y.toInt() - offsetCobra.y
                cobraRect.right = (x + cobra.getWidth() - offsetCobra.x).toInt()
                cobraRect.bottom = (y + cobra.getHeight() - offsetCobra.y).toInt()
                this.invalidate()
            }

            println("$lion_touched, $rabbit_touched, $cobra_touched")
            /*********************************************************************************************/
            if (!in_arena) {
                if (lion_touched) {
                    var delta_x =  (leoLeftOrigin - lion.left).toFloat()
                    var delta_y =  (leoTopOrigin - lion.top).toFloat()
                    var moveAnim = TranslateAnimation(0f, delta_x, 0f, delta_y)
                    moveAnim.duration = 1000
                    //moveAnim.fillAfter = true
                    var handler = MoveBackHandler1()
                    moveAnim.setAnimationListener(handler)
                    lion.startAnimation(moveAnim)
                    this.invalidate()

                } else if (rabbit_touched) {
                    var delta_x =  (rabLeftOrigin - rabbit.left).toFloat()
                    var delta_y =  (rabTopOrigin - rabbit.top).toFloat()
                    var moveAnim = TranslateAnimation(0f, delta_x, 0f, delta_y)
                    moveAnim.duration = 1000
                    //moveAnim.fillAfter = true
                    var handler = MoveBackHandler2()
                    moveAnim.setAnimationListener(handler)
                    rabbit.startAnimation(moveAnim)
                    this.invalidate()

                } else if (cobra_touched) {
                    var delta_x =  (cobLeftOrigin - cobra.left).toFloat()
                    var delta_y =  (cobTopOrigin - cobra.top).toFloat()
                    var moveAnim = TranslateAnimation(0f, delta_x, 0f, delta_y)
                    moveAnim.duration = 1000
                    //moveAnim.fillAfter = true
                    var handler = MoveBackHandler3()
                    moveAnim.setAnimationListener(handler)
                    cobra.startAnimation(moveAnim)
                    this.invalidate()
                }
            }

            lion_touched = false
            rabbit_touched = false
            cobra_touched = false
        }
        /*********************************************************************************************/
        return true
    }

    //Main Animations / Sequences
    inner class Intro : Animation.AnimationListener {

        override fun onAnimationRepeat(animation: Animation?)
        {}
        override fun onAnimationEnd(animation: Animation?)
        {
            var pscore = MainActivity.getInstance().findViewById<TextView>(R.id.pscore)
            var score = MainActivity.getInstance().findViewById<TextView>(R.id.score)
            var title1 : ImageView? = MainActivity.getInstance().findViewById(R.id.title1)
            var phone = MainActivity.getInstance().findViewById<TextView>(R.id.phone)
            var player = MainActivity.getInstance().findViewById<TextView>(R.id.player)
            var myFadeOut = AnimationUtils.loadAnimation(MainActivity.getInstance(), R.anim.fade_out)
            var myFadeIn = AnimationUtils.loadAnimation(MainActivity.getInstance(), R.anim.fade_in)
            var animHandler1 = FadeOutHandler()
            var animHandler2 = FadeInHandler1()
            myFadeOut.setAnimationListener(animHandler1)
            myFadeIn.setAnimationListener(animHandler2)
            title1?.startAnimation(myFadeOut)
            phone.startAnimation(myFadeIn)
            player.startAnimation(myFadeIn)
            pscore.startAnimation(myFadeIn)
            score.startAnimation(myFadeIn)
        }
        override fun onAnimationStart(animation: Animation?)
        {}
    }



    inner class TieHandler : Animation.AnimationListener {
        var enemy = MainActivity.getInstance().findViewById<ImageView>(R.id.enemy)
        var results = MainActivity.getInstance().findViewById<TextView>(R.id.results)
        var chosen = MainActivity.getInstance().findViewById<ImageView>(R.id.chosen)
        override fun onAnimationRepeat(p0: Animation?) {

        }

        override fun onAnimationEnd(p0: Animation?) {
            var myFadeIn = AnimationUtils.loadAnimation(MainActivity.getInstance(), R.anim.fade_in)
            var animHandler1 = FadeInHandler0()
            myFadeIn.setAnimationListener(animHandler1)
            results.startAnimation(myFadeIn)
        }

        override fun onAnimationStart(p0: Animation?) {
            var myFadeOut = AnimationUtils.loadAnimation(MainActivity.getInstance(), R.anim.fade_out)
            var animHandler = FadeOutHandler2()
            myFadeOut.setAnimationListener(animHandler)
            enemy.startAnimation(myFadeOut)
            //chosen.visibility = VISIBLE
        }
    }

    inner class PlayerWinsHandler : Animation.AnimationListener {
        var chosen = MainActivity.getInstance().findViewById<ImageView>(R.id.chosen)
        var enemy = MainActivity.getInstance().findViewById<ImageView>(R.id.enemy)
        var results = MainActivity.getInstance().findViewById<TextView>(R.id.results)

        override fun onAnimationRepeat(p0: Animation?) {

        }

        override fun onAnimationEnd(p0: Animation?) {
            var myFadeIn = AnimationUtils.loadAnimation(MainActivity.getInstance(), R.anim.fade_in)
            var animHandler1 = FadeInHandler3()
            myFadeIn.setAnimationListener(animHandler1)
            results.startAnimation(myFadeIn)
        }

        override fun onAnimationStart(p0: Animation?) {
            var myFadeOut = AnimationUtils.loadAnimation(MainActivity.getInstance(), R.anim.fade_out)
            var animHandler = FadeOutHandler()
            myFadeOut.setAnimationListener(animHandler)
            enemy.startAnimation(myFadeOut)
        }
    }

    inner class EnemyWinsHandler : Animation.AnimationListener {
        var chosen = MainActivity.getInstance().findViewById<ImageView>(R.id.chosen)
        var enemy = MainActivity.getInstance().findViewById<ImageView>(R.id.enemy)
        var results = MainActivity.getInstance().findViewById<TextView>(R.id.results)

        override fun onAnimationRepeat(p0: Animation?) {
        }

        override fun onAnimationEnd(p0: Animation?) {
            var myFadeIn = AnimationUtils.loadAnimation(MainActivity.getInstance(), R.anim.fade_in)
            var animHandler1 = FadeInHandler4()
            myFadeIn.setAnimationListener(animHandler1)
            results.startAnimation(myFadeIn)
        }

        override fun onAnimationStart(p0: Animation?) {
            var myFadeOut = AnimationUtils.loadAnimation(MainActivity.getInstance(), R.anim.fade_out)
            var animHandler = FadeOutHandler()
            myFadeOut.setAnimationListener(animHandler)
           chosen.startAnimation(myFadeOut)
        }
    }

    //Base Animations
    inner class FadeInHandler1 : Animation.AnimationListener {

        override fun onAnimationRepeat(animation: Animation?)
        {}
        override fun onAnimationEnd(animation: Animation?)
        {
            var pscore = MainActivity.getInstance().findViewById<TextView>(R.id.pscore)
            var score = MainActivity.getInstance().findViewById<TextView>(R.id.score)
            var phone = MainActivity.getInstance().findViewById<TextView>(R.id.phone)
            var player = MainActivity.getInstance().findViewById<TextView>(R.id.player)
            phone.visibility = View.VISIBLE
            player.visibility = View.VISIBLE
            pscore.visibility = View.VISIBLE
            score.visibility = View.VISIBLE

        }
        override fun onAnimationStart(animation: Animation?)
        {}
    }

    inner class FadeInHandler2 : Animation.AnimationListener {
        private var chosen = MainActivity.getInstance().findViewById<ImageView>(R.id.chosen)
        private var enemy = MainActivity.getInstance().findViewById<ImageView>(R.id.enemy)
        private var results = MainActivity.getInstance().findViewById<TextView>(R.id.results)
        private var pscore = MainActivity.getInstance().findViewById<TextView>(R.id.pscore) //player score
        private var score = MainActivity.getInstance().findViewById<TextView>(R.id.score) //phone score
        private var selectedItem = chosen
        private var enemySpawned = enemy

        override fun onAnimationRepeat(p0: Animation?) {}

        override fun onAnimationEnd(p0: Animation?) {
            //Translation move
               if(selectedItem?.tag == "Lion" && enemySpawned?.tag == "Rabbit"){
                //Player wins
                playerscore++
                score.setText(playerscore.toString())
                results.setText("Lion defeats Rabbit - You Win!")
                var delta_x = (enemy.left - chosen.left).toFloat()
                var delta_y = (enemy.top - chosen.top).toFloat()
                var moveAnim = TranslateAnimation(0f, delta_x, 0f, delta_y)
                var handler = PlayerWinsHandler()
                moveAnim.setAnimationListener(handler)
                chosen.startAnimation(moveAnim)
            }
            else if(selectedItem?.tag == "Lion" && enemySpawned?.tag == "Cobra"){
            //Player loses
                computerscore++
                pscore.setText(computerscore.toString())
                results.setText("Cobra defeats Lion - You Lose!")
                var delta_x = (chosen.left - enemy.left).toFloat()
                var delta_y = (chosen.top - enemy.top).toFloat()
                var moveAnim = TranslateAnimation(0f, delta_x, 0f, delta_y)
                var handler = EnemyWinsHandler()
                moveAnim.setAnimationListener(handler)
                enemy.startAnimation(moveAnim)
            }
            else if(selectedItem?.tag == "Rabbit" && enemySpawned?.tag == "Lion"){
                //Player loses
                computerscore++
                pscore.setText(computerscore.toString())
                results.setText("Lion defeats Rabbit - You Lose!")
                var delta_x = (chosen.left - enemy.left).toFloat()
                var delta_y = (chosen.top - enemy.top).toFloat()
                var moveAnim = TranslateAnimation(0f, delta_x, 0f, delta_y)
                var handler = EnemyWinsHandler()
                moveAnim.setAnimationListener(handler)
                enemy.startAnimation(moveAnim)
            }
            else if(selectedItem?.tag == "Rabbit" && enemySpawned?.tag == "Cobra"){
                //Player wins
                playerscore++
                score.setText(playerscore.toString())
                results.setText("Rabbit defeats Cobra - You Win!")
                var delta_x = (enemy.left - chosen.left).toFloat()
                var delta_y = (enemy.top - chosen.top).toFloat()
                var moveAnim = TranslateAnimation(0f, delta_x, 0f, delta_y)
                var handler = PlayerWinsHandler()
                moveAnim.setAnimationListener(handler)
                chosen.startAnimation(moveAnim)
            }
            else if(selectedItem?.tag == "Cobra" && enemySpawned?.tag == "Lion"){
                //Player wins
                playerscore++
                score.setText(playerscore.toString())
                results.setText("Cobra defeats Lion - You Win!")
                var delta_x = (enemy.left - chosen.left).toFloat()
                var delta_y = (enemy.top - chosen.top).toFloat()
                var moveAnim = TranslateAnimation(0f, delta_x, 0f, delta_y)
                var handler = PlayerWinsHandler()
                moveAnim.setAnimationListener(handler)
                chosen.startAnimation(moveAnim)
            }
            else if(selectedItem?.tag == "Cobra" && enemySpawned?.tag == "Rabbit"){
                //Player loses
                computerscore++
                pscore.setText(computerscore.toString())
                results.setText("Rabbit defeats Cobra - You Lose!")
                var delta_x = (chosen.left - enemy.left).toFloat()
                var delta_y = (chosen.top - enemy.top).toFloat()
                var moveAnim = TranslateAnimation(0f, delta_x, 0f, delta_y)
                var handler = EnemyWinsHandler()
                moveAnim.setAnimationListener(handler)
                enemy.startAnimation(moveAnim)
            }
            else {
                //Tie
                var myFadeOut = AnimationUtils.loadAnimation(MainActivity.getInstance(), R.anim.fade_out)
                results.setText("Tie!")
                var handler = TieHandler()
                myFadeOut.setAnimationListener(handler)
                chosen.startAnimation(myFadeOut)

            }
        }

        override fun onAnimationStart(p0: Animation?) {}

    }

    inner class FadeInHandler3 : Animation.AnimationListener {
        var chosen = MainActivity.getInstance().findViewById<ImageView>(R.id.chosen)
        var results = MainActivity.getInstance().findViewById<TextView>(R.id.results)

        override fun onAnimationRepeat(animation: Animation?)
        {}
        override fun onAnimationEnd(animation: Animation?)
        {
            //fade out chosen
            var myFadeOut2 = AnimationUtils.loadAnimation(MainActivity.getInstance(), R.anim.fade_out)
            var animHandler = FadeOutHandler2()
            myFadeOut2.setAnimationListener(animHandler)
            chosen.startAnimation(myFadeOut2)

        }
        override fun onAnimationStart(animation: Animation?)
        {}
    }

    inner class FadeInHandler4 : Animation.AnimationListener {
        var results = MainActivity.getInstance().findViewById<TextView>(R.id.results)
        var enemy = MainActivity.getInstance().findViewById<ImageView>(R.id.enemy)


        override fun onAnimationRepeat(animation: Animation?)
        {}
        override fun onAnimationEnd(animation: Animation?)
        {
            //fade out enemy that won
            var myFadeOut2 = AnimationUtils.loadAnimation(MainActivity.getInstance(), R.anim.fade_out)
            var animHandler = FadeOutHandler2()
            myFadeOut2.setAnimationListener(animHandler)
            enemy.startAnimation(myFadeOut2)

        }
        override fun onAnimationStart(animation: Animation?)
        {}
    }

    inner class FadeInHandler0 : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation?)
        {}
        override fun onAnimationEnd(animation: Animation?)
        {}
        override fun onAnimationStart(animation: Animation?)
        {}
    }

    inner class FadeOutHandler : Animation.AnimationListener {

        override fun onAnimationRepeat(animation: Animation?)
        {}
        override fun onAnimationEnd(animation: Animation?)
        {}
        override fun onAnimationStart(animation: Animation?)
        {}
    }

    inner class FadeOutHandler2 : Animation.AnimationListener {
        private var lion = MainActivity.getInstance().findViewById<ImageView>(R.id.lion)
        private var rabbit = MainActivity.getInstance().findViewById<ImageView>(R.id.rabbit)
        private var cobra = MainActivity.getInstance().findViewById<ImageView>(R.id.cobra)
        private var chosen = MainActivity.getInstance().findViewById<ImageView>(R.id.chosen)
        private var enemy = MainActivity.getInstance().findViewById<ImageView>(R.id.enemy)
        private var results = MainActivity.getInstance().findViewById<TextView>(R.id.results)

        override fun onAnimationRepeat(animation: Animation?)
        {}
        override fun onAnimationEnd(animation: Animation?)
        {
            var myFadeIn = AnimationUtils.loadAnimation(MainActivity.getInstance(), R.anim.fade_in)
            if (chosen.tag == "Lion") {
                var animHandler = RestartHandler1()
                myFadeIn.setAnimationListener(animHandler)
                lion.startAnimation(myFadeIn)

            } else if (chosen.tag == "Rabbit") {
                var animHandler = RestartHandler2()
                myFadeIn.setAnimationListener(animHandler)
                rabbit.startAnimation(myFadeIn)

            } else if (chosen.tag == "Cobra") {
                var animHandler = RestartHandler3()
                myFadeIn.setAnimationListener(animHandler)
                cobra.startAnimation(myFadeIn)
            }
            chosen.visibility = View.VISIBLE
            enemy.visibility = View.INVISIBLE
        }
        override fun onAnimationStart(animation: Animation?)
        {
            var myFadeOut = AnimationUtils.loadAnimation(MainActivity.getInstance(), R.anim.fade_out)
            var animHandler = FadeOutHandler()
            myFadeOut.setAnimationListener(animHandler)
            results.startAnimation(myFadeOut)
        }
    }

    private fun lionStart() {
        var lion = MainActivity.getInstance().findViewById<ImageView>(R.id.lion)
        var chosen = MainActivity.getInstance().findViewById<ImageView>(R.id.chosen)
        var delta_x =  (chosen.left - lion.left).toFloat()
        var delta_y =  (chosen.top - lion.top).toFloat()
        var moveAnim = TranslateAnimation(0f, delta_x, 0f, delta_y)
        moveAnim.duration = 1000
        var handler = MoveForwardHandler1()
        moveAnim.setAnimationListener(handler)
        lion.startAnimation(moveAnim)
    }
    private fun rabbitStart() {
        var rabbit = MainActivity.getInstance().findViewById<ImageView>(R.id.rabbit)
        var chosen = MainActivity.getInstance().findViewById<ImageView>(R.id.chosen)
        var delta_x =  (chosen.left - rabbit.left).toFloat()
        var delta_y =  (chosen.top - rabbit.top).toFloat()
        var moveAnim = TranslateAnimation(0f, delta_x, 0f, delta_y)
        moveAnim.duration = 1000
        var handler = MoveForwardHandler2()
        moveAnim.setAnimationListener(handler)
        rabbit.startAnimation(moveAnim)
    }
    private fun cobraStart() {
        var cobra = MainActivity.getInstance().findViewById<ImageView>(R.id.cobra)
        var chosen = MainActivity.getInstance().findViewById<ImageView>(R.id.chosen)
        var delta_x =  (chosen.left - cobra.left).toFloat()
        var delta_y =  (chosen.top - cobra.top).toFloat()
        var moveAnim = TranslateAnimation(0f, delta_x, 0f, delta_y)
        moveAnim.duration = 1000
        var handler = MoveForwardHandler3()
        moveAnim.setAnimationListener(handler)
        cobra.startAnimation(moveAnim)

    }
//insert the icons back to position
    inner class RestartHandler1 : Animation.AnimationListener {
        var lion = MainActivity.getInstance().findViewById<ImageView>(R.id.lion)

        override fun onAnimationRepeat(animation: Animation?)
        {}
        override fun onAnimationEnd(animation: Animation?)
        {
            lionRect = Rect(lionOrigin.left,lionOrigin.top,lionOrigin.right,lionOrigin.bottom)
            lion.left = lionOrigin.left
            lion.top = lionOrigin.top
            lion.right = lionOrigin.right
            lion.bottom = lionOrigin.bottom

        }
        override fun onAnimationStart(animation: Animation?)
        {
            var myFadeIn = AnimationUtils.loadAnimation(MainActivity.getInstance(), R.anim.fade_in)
            var animHandler1 = FadeInHandler0()
            myFadeIn.setAnimationListener(animHandler1)
            lion.startAnimation(myFadeIn)
            lion.visibility = VISIBLE
        }
    }
    inner class RestartHandler2 : Animation.AnimationListener {
        var rabbit = MainActivity.getInstance().findViewById<ImageView>(R.id.rabbit)

        override fun onAnimationRepeat(animation: Animation?)
        {}
        override fun onAnimationEnd(animation: Animation?)
        {
            rabbitRect = Rect(rabbitOrigin.left,rabbitOrigin.top,rabbitOrigin.right,rabbitOrigin.bottom)
            rabbit.left = rabbitOrigin.left
            rabbit.top = rabbitOrigin.top
            rabbit.right = rabbitOrigin.right
            rabbit.bottom = rabbitOrigin.bottom


        }
        override fun onAnimationStart(animation: Animation?)
        {
            var myFadeIn = AnimationUtils.loadAnimation(MainActivity.getInstance(), R.anim.fade_in)
            var animHandler1 = FadeInHandler0()
            myFadeIn.setAnimationListener(animHandler1)
            rabbit.startAnimation(myFadeIn)
            rabbit.visibility = VISIBLE
        }
    }
    inner class RestartHandler3 : Animation.AnimationListener {
        var cobra = MainActivity.getInstance().findViewById<ImageView>(R.id.cobra)

        override fun onAnimationRepeat(animation: Animation?)
        {}
        override fun onAnimationEnd(animation: Animation?)
        {
            cobraRect = Rect(cobraOrigin.left,cobraOrigin.top,cobraOrigin.right,cobraOrigin.bottom)
            cobra.left = cobraOrigin.left
            cobra.top = cobraOrigin.top
            cobra.right = cobraOrigin.right
            cobra.bottom = cobraOrigin.bottom

        }
        override fun onAnimationStart(animation: Animation?)
        {
            var myFadeIn = AnimationUtils.loadAnimation(MainActivity.getInstance(), R.anim.fade_in)
            var animHandler1 = FadeInHandler0()
            myFadeIn.setAnimationListener(animHandler1)
            cobra.startAnimation(myFadeIn)
            cobra.visibility = VISIBLE
        }
    }


    inner class MoveBackHandler1 : Animation.AnimationListener {
        var lion = MainActivity.getInstance().findViewById<ImageView>(R.id.lion)

        override fun onAnimationRepeat(animation: Animation?)
        {}
        override fun onAnimationEnd(animation: Animation?)
        {
            lionRect = Rect(lionOrigin.left,lionOrigin.top,lionOrigin.right,lionOrigin.bottom)
            lion.left = lionOrigin.left
            lion.top = lionOrigin.top
            lion.right = lionOrigin.right
            lion.bottom = lionOrigin.bottom

        }
        override fun onAnimationStart(animation: Animation?)
        {}
    }

    inner class MoveBackHandler2 : Animation.AnimationListener {
        var rabbit = MainActivity.getInstance().findViewById<ImageView>(R.id.rabbit)

        override fun onAnimationRepeat(animation: Animation?)
        {}
        override fun onAnimationEnd(animation: Animation?)
        {
            rabbitRect = Rect(rabbitOrigin.left,rabbitOrigin.top,rabbitOrigin.right,rabbitOrigin.bottom)
            rabbit.left = rabbitOrigin.left
            rabbit.top = rabbitOrigin.top
            rabbit.right = rabbitOrigin.right
            rabbit.bottom = rabbitOrigin.bottom

        }
        override fun onAnimationStart(animation: Animation?)
        {}
    }


    inner class MoveBackHandler3 : Animation.AnimationListener {
        var cobra = MainActivity.getInstance().findViewById<ImageView>(R.id.cobra)

        override fun onAnimationRepeat(animation: Animation?)
        {}
        override fun onAnimationEnd(animation: Animation?)
        {
            cobraRect = Rect(cobraOrigin.left,cobraOrigin.top,cobraOrigin.right,cobraOrigin.bottom)
            cobra.left = cobraOrigin.left
            cobra.top = cobraOrigin.top
            cobra.right = cobraOrigin.right
            cobra.bottom = cobraOrigin.bottom

        }
        override fun onAnimationStart(animation: Animation?)
        {}
    }

    inner class MoveForwardHandler1 : Animation.AnimationListener {
        var lion = MainActivity.getInstance().findViewById<ImageView>(R.id.lion)
        var chosen = MainActivity.getInstance().findViewById<ImageView>(R.id.chosen)

        override fun onAnimationRepeat(animation: Animation?)
        {}
        override fun onAnimationEnd(animation: Animation?)
        {
            lionRect = Rect(chosen.left,chosen.top,chosen.right,chosen.bottom)
            lion.left = chosen.left
            lion.top = chosen.top
            lion.right = chosen.right
            lion.bottom = chosen.bottom
            chosen.setImageResource(R.drawable.lion0)
            lion.visibility = View.INVISIBLE
            chosen.visibility = View.VISIBLE

        }
        override fun onAnimationStart(animation: Animation?)
        {}
    }

    inner class MoveForwardHandler2 : Animation.AnimationListener {
        var rabbit = MainActivity.getInstance().findViewById<ImageView>(R.id.rabbit)
        var chosen = MainActivity.getInstance().findViewById<ImageView>(R.id.chosen)

        override fun onAnimationRepeat(animation: Animation?)
        {}
        override fun onAnimationEnd(animation: Animation?)
        {
            rabbitRect = Rect(chosen.left,chosen.top,chosen.right,chosen.bottom)
            rabbit.left = chosen.left
            rabbit.top = chosen.top
            rabbit.right = chosen.right
            rabbit.bottom = chosen.bottom
            chosen.setImageResource(R.drawable.rabbit)
            chosen.scaleX = -1.0f   //Flips horizontally
            chosenFlipped = true
            rabbit.visibility = View.INVISIBLE
            chosen.visibility = View.VISIBLE

        }
        override fun onAnimationStart(animation: Animation?)
        {}
    }

    inner class MoveForwardHandler3 : Animation.AnimationListener {
        var cobra = MainActivity.getInstance().findViewById<ImageView>(R.id.cobra)
        var chosen = MainActivity.getInstance().findViewById<ImageView>(R.id.chosen)

        override fun onAnimationRepeat(animation: Animation?)
        {}
        override fun onAnimationEnd(animation: Animation?)
        {
            cobraRect = Rect(chosen.left,chosen.top,chosen.right,chosen.bottom)
            cobra.left = chosen.left
            cobra.top = chosen.top
            cobra.right = chosen.right
            cobra.bottom = chosen.bottom
            chosen.setImageResource(R.drawable.cobra)
            chosenFlipped = true
            cobra.visibility = View.INVISIBLE
            chosen.visibility = View.VISIBLE

        }
        override fun onAnimationStart(animation: Animation?)
        {}
    }
}