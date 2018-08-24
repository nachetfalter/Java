package whatever.gridlock.model;
import java.lang.System;
import java.util.*;

class NormalGridLock implements Generator<Car> {

    private class Choice {
        String direct;
        String facing;
        int size;
        Coordination critical;

        Choice(String d, String fac, int size, Coordination critical) {
            this.direct = d;
            this.facing = fac;
            this.size = size;
            this.critical = critical;
        }
    }

    @Override
    public int[][] generate(int col, int row, int[][] board, List<Car> entities) {
        int x = 0;
        int y = 0;
        initMatrix(col, row, board);
        Car player = generatePlayer(board, board.length);
        System.out.println("Player: " + player.getHead().getX() + " " + player.getHead().getY());
        entities.add(player);
        Coordination last = null;
        for (int i = player.getHead().getX() + 1; i < 5; i++) {
            board[2][i] = -1;
        }
        for (int i = 5; i >= 0; i--) {
            if (i == player.getHead().getX()) continue;
            if(board[2][i] == -1) board[2][i] = 0;
            if (board[2][i] == 0) {
                Coordination critical = new Coordination(i, player.getHead().getY());
                while (critical != null) {
                    critical = generateCar(critical, board, entities);
                    //if(critical != null){
                        //last = critical;
                        // System.out.println(critical.getX() + " " + critical.getY());
                    //}
                }
                //board[last.getY()][last.getX()] = -1;
            }
        }

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (board[i][j] <= 0) {
                    Coordination critical = new Coordination(i, j);
                    while (critical != null) {
                        critical = generateCar(critical, board, entities);
                        //if(critical != null){
                            //last = critical;
                            //System.out.println(critical.getX() + " " + critical.getY());
                       // }
                    }
                    //board[last.getY()][last.getX()] = -1;
                }
            }
        }

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (board[i][j] == -1) {
                    board[i][j] = 0;
                }
                //System.out.print(board[i][j]);
            }
            //System.out.println();
        }
        return board;
    }
/*
    private void gen(Coordination target, int[][] board, List<Car> entities) {
        Set<Coordination> result = generateCar(target, board, entities);
        assert result != null;
        for (Coordination j : result) {
            System.out.print(j.toString() + " ");
        }
        System.out.println();
        while (!result.isEmpty()) {
            Set<Coordination> newLevel = new HashSet<>();
            for (Coordination i : result) {
                if (board[i.getY()][i.getX()] == -1) board[i.getY()][i.getX()] = 0;
                Set<Coordination> x = generateCar(i, board, entities);
                if (x != null) {
                    for (Coordination j : newLevel) {
                        board[j.getY()][j.getX()] = -1;
                    }
                    newLevel.addAll(x);
                }
            }
            for (Coordination j : newLevel) {
                board[j.getY()][j.getX()] = -1;
                System.out.print(j.toString() + " ");
            }
            System.out.println();
            result = newLevel;
        }
    }

    private void search(List<Choice> choiceSet, String dir, String facing, int size, Coordination co) {
        boolean test = false;
        for(Choice c:choiceSet) {
            if (c.direct.equals(dir) && c.facing.equals(facing)) {
                c.critical.add(co);
                test = true;
            }
        }
        if (!test) {
            Choice c = new Choice(dir, facing, size);
            c.critical.add(co);
            choiceSet.add(c);
        }
    }
*/
    private boolean validate(int[][] board, Coordination target, String facing, int size, String direction) {
        //System.out.println("miao " + board.length + " " + target.getX() + " " + target.getY() + " " + size);
        if (direction.equals("Vertical")) {
            switch (facing) {
                case "Head":
                    return target.getY() + size - 1 < board.length &&
                            board[target.getY() + size - 1][target.getX()] == 0 &&
                            board[target.getY() + size - 2][target.getX()] == 0;
                case "Body":
                    return target.getY() - 1 >= 0 && target.getY() + 1 < board.length &&
                            board[target.getY() + 1][target.getX()] == 0 &&
                            board[target.getY() - 1][target.getX()] == 0;
                default:
                    return target.getY() - size + 1 >= 0 && target.getY() - size + 2 >= 0 &&
                            board[target.getY() - size + 1][target.getX()] == 0 &&
                            board[target.getY() - size + 2][target.getX()] == 0;
            }
        }
        else {
            switch (facing) {
                case "Tail":
                    return target.getX() + size - 1 < board.length && target.getX() + size - 2 < board.length &&
                            board[target.getY()][target.getX() + size - 1] == 0 &&
                            board[target.getY()][target.getX() + size - 2] == 0;
                case "Body":
                    return target.getX() - 1 >= 0 && target.getX() + 1 < board.length &&
                            board[target.getY()][target.getX() + 1] == 0 &&
                            board[target.getY()][target.getX() - 1] == 0;
                default:
                    return target.getX() - size + 1 >= 0 && target.getX() - size + 2 >= 0 &&
                            board[target.getY()][target.getX() - size + 1] == 0 &&
                            board[target.getY()][target.getX() - size + 2] == 0;
            }
        }
    }

    private void block(Coordination from, Coordination to, int[][] board) {
        if (from.getX() == to.getX()) {
            if (from.getY() > to.getY()) {
                for (int i = to.getY(); i < from.getY(); i++) {
                    board[i][to.getX()] = -1;
                }
            }
            else {
                for (int i = from.getY() + 1; i <= to.getY(); i++) {
                    board[i][to.getX()] = -1;
                }
            }
        }
        else {
            if (from.getX() > to.getX()) {
                for (int i = to.getX(); i < from.getX(); i++) {
                    board[to.getY()][i] = -1;
                }
            }
            else {
                for (int i = from.getX() + 1; i <= to.getX(); i++) {
                    board[to.getY()][i] = -1;
                }
            }
        }

    }

    private Coordination generateCar(Coordination target, int[][] board, List<Car> entities) {
        if (board[target.getY()][target.getX()] == 1) return null;
        board[target.getY()][target.getX()] = 0;
        //System.out.println(target.getX() + " miao " + target.getY());
        Random r = new Random();
        List<Choice> choiceSet = new ArrayList<>();
        boolean flag = true;
        String dir = "Vertical";
        for (int i = 2; i <= 3; i++) {
            if (target.getY()-i >= 0 && board[target.getY()-i][target.getX()] == 0) {
                for (int j = target.getY()-1; j >= target.getY()-i; j--) {
                    if (board[j][target.getX()] != 0) {
                        flag = false;
                        break;
                    }
                }
            }
            else {
                flag = false;
            }
            if (flag) {
                Coordination critical1 = new Coordination(target.getX(), target.getY()-i);
                if (validate(board, target, "Tail", i, dir)) choiceSet.add(new Choice(dir, "Tail", i, critical1));
                if (validate(board, target, "Head", i, dir)) choiceSet.add(new Choice(dir, "Head", i, critical1));

            }

            flag = true;
            if (target.getY()+i < board.length && board[target.getY()+i][target.getX()] == 0) {
                for (int j = target.getY()+1; j <= target.getY()+i; j++) {
                    if (board[j][target.getX()] != 0) {
                        flag = false;
                        break;
                    }
                }
            }
            else {
                flag = false;
            }
            if (flag) {
                Coordination critical = new Coordination(target.getX(), target.getY()+i);
                if (validate(board, target, "Head", i, dir)) choiceSet.add(new Choice(dir, "Head", i, critical));
                if (validate(board, target, "Tail", i, dir)) choiceSet.add(new Choice(dir, "Tail", i, critical));
            }

            if (i == 3) {
                flag = true;
                if (target.getY() + 3 < board.length && board[target.getY() + 3][target.getX()] == 0) {
                    for (int j = target.getY()+1; j <= target.getY() + 3; j++) {
                        if (board[j][target.getX()] != 0) {
                            flag = false;
                            break;
                        }
                    }
                } else {
                    flag = false;
                }
                if (flag) {
                    Coordination critical = new Coordination(target.getX(), target.getY() + 3);
                    if (validate(board, target, "Body", i, dir)) choiceSet.add(new Choice(dir, "Body", i, critical));
                }

                flag = true;
                if (target.getY() - 3 >= 0 && board[target.getY() - 3][target.getX()] == 0) {
                    for (int j = target.getY()-1; j >= target.getY() - 3; j--) {
                        if (board[j][target.getX()] != 0) {
                            flag = false;
                            break;
                        }
                    }
                } else {
                    flag = false;
                }
                if (flag) {
                    Coordination critical = new Coordination(target.getX(), target.getY() - 3);
                    if (validate(board, target, "Body", i, dir)) choiceSet.add(new Choice(dir, "Body", i, critical));
                }
            }
        }
        flag = true;
        if (target.getY() != 2) {
            dir = "Horizontal";
            for (int i = 2; i <= 3; ++i) {
                if (target.getX()+i < board[0].length && board[target.getY()][target.getX()+i] == 0) {
                    for (int j = target.getX()+1; j <= target.getX()+i; j++) {
                        if (board[target.getY()][j] != 0) {
                            flag = false;
                            break;
                        }
                    }
                }
                else {
                    flag = false;
                }
                if (flag) {
                    Coordination critical = new Coordination(target.getX()+i, target.getY());
                    if (validate(board, target, "Head", i, dir)) choiceSet.add(new Choice(dir, "Head", i, critical));
                    if (validate(board, target, "Tail", i, dir)) choiceSet.add(new Choice(dir, "Tail", i, critical));
                }

                flag = true;
                if (target.getX()-i >= 0 && board[target.getY()][target.getX()-i] == 0) {
                    for (int j = target.getX()-1; j >= target.getX()-i; j--) {
                        if (board[target.getY()][j] != 0) {
                            flag = false;
                            break;
                        }
                    }
                }
                else {
                    flag = false;
                }
                if (flag) {
                    Coordination critical = new Coordination(target.getX()-i, target.getY());
                    if (validate(board, target, "Head", i, dir)) choiceSet.add(new Choice(dir, "Head", i, critical));
                    if (validate(board, target, "Tail", i, dir)) choiceSet.add(new Choice(dir, "Tail", i, critical));
                }

                if (i == 3) {
                    flag = true;
                    if (target.getX() + 3 < board.length && board[target.getY()][target.getX()+3] == 0) {
                        for (int j = target.getX()+1; j <= target.getX() + 3; j++) {
                            if (board[target.getY()][j] != 0) {
                                flag = false;
                                break;
                            }
                        }
                    } else {
                        flag = false;
                    }
                    if (flag) {
                        Coordination critical = new Coordination(target.getX() + 3, target.getY());
                        if (validate(board, target, "Body", i, dir)) choiceSet.add(new Choice(dir, "Body", i, critical));
                    }

                    flag = true;
                    if (target.getX() - 3 >= 0 && board[target.getY()][target.getX()-3] == 0) {
                        for (int j = target.getX()-1; j <= target.getX() - 3; j--) {
                            if (board[target.getY()][j] != 0) {
                                flag = false;
                            }
                        }
                    } else {
                        flag = false;
                    }
                    if (flag) {
                        Coordination critical = new Coordination(target.getX() - 3, target.getY());
                        if (validate(board, target, "Body", i, dir)) choiceSet.add(new Choice(dir, "Body", i, critical));
                    }
                }
            }
        }

        if (choiceSet.size() == 0) {
            board[target.getY()][target.getX()] = -1;
            return null;
        }
        int index = r.nextInt(choiceSet.size());
        /*
        for (Choice i : choiceSet) {
            i.print();
        }
        */
        Choice result = choiceSet.get(index);
        Car newCar;
        //System.out.println();
        //result.print();
        //System.out.println();
        Coordination head;
        Coordination tail;
        Coordination body;
        if (result.size == 2) {
            if (result.facing.equals("Head")) {
                head = target;
                if (result.direct.equals("Horizontal")) {
                    tail = new Coordination(target.getX()-1, target.getY());
                    newCar = new Car(false, "Horizontal", 2);
                }
                else {
                    tail = new Coordination(target.getX(), target.getY()+1);
                    newCar = new Car(false, "Vertical", 2);
                }
            }
            else {
                tail = target;
                if (result.direct.equals("Horizontal")) {
                    head = new Coordination(target.getX()+1, target.getY());
                    newCar = new Car(false, "Horizontal", 2);
                }
                else {
                    head = new Coordination(target.getX(), target.getY()-1);
                    newCar = new Car(false, "Vertical", 2);
                }
            }
            newCar.getPos().add(head);
            newCar.getPos().add(tail);
            board[head.getY()][head.getX()] = 1;
            board[tail.getY()][tail.getX()] = 1;
        }
        else {
            switch (result.facing) {
                case "Head":
                    head = target;
                    if (result.direct.equals("Horizontal")) {
                        body = new Coordination(target.getX() - 1, target.getY());
                        tail = new Coordination(target.getX() - 2, target.getY());
                        newCar = new Car(false, "Horizontal", 3);
                    } else {
                        body = new Coordination(target.getX(), target.getY() + 1);
                        tail = new Coordination(target.getX(), target.getY() + 2);
                        newCar = new Car(false, "Vertical", 3);
                    }
                    break;
                case "Body":
                    if (result.direct.equals("Horizontal")) {
                        body = new Coordination(target.getX(), target.getY());
                        head = new Coordination(target.getX() + 1, target.getY());
                        tail = new Coordination(target.getX() - 1, target.getY());
                        newCar = new Car(false, "Horizontal", 3);
                    } else {
                        body = new Coordination(target.getX(), target.getY());
                        head = new Coordination(target.getX(), target.getY() - 1);
                        tail = new Coordination(target.getX(), target.getY() + 1);
                        newCar = new Car(false, "Vertical", 3);
                    }
                    break;
                default:
                    tail = target;
                    if (result.direct.equals("Horizontal")) {
                        body = new Coordination(target.getX() + 1, target.getY());
                        head = new Coordination(target.getX() + 2, target.getY());
                        newCar = new Car(false, "Horizontal", 3);
                    } else {
                        body = new Coordination(target.getX(), target.getY() - 1);
                        head = new Coordination(target.getX(), target.getY() - 2);
                        newCar = new Car(false, "Vertical", 3);
                    }
                    break;
            }
            newCar.getPos().add(head);
            newCar.getPos().add(body);
            newCar.getPos().add(tail);
            board[head.getY()][head.getX()] = 1;
            board[body.getY()][body.getX()] = 1;
            board[tail.getY()][tail.getX()] = 1;
        }
        System.out.print("Move Car ");
        newCar.print();
        System.out.println(" to " + "[" + result.critical.getX() + "," + result.critical.getY() + "]");
        entities.add(newCar);
        System.out.println("Before");
        for (int i = 0; i < 6; ++i) {
            for (int j = 0; j < 6; ++ j) {
                System.out.print(board[i][j]);
            }
            System.out.println();
        }
        System.out.println();
        if (result.direct.equals("Vertical")) {
            if (result.critical.getY() < newCar.getHead().getY()) {
                block(newCar.getHead(), result.critical, board);
            }
            else {
                block(newCar.getTail(), result.critical, board);
            }
        }
        else {
            if (result.critical.getX() > newCar.getHead().getX()) {
                block(newCar.getHead(), result.critical, board);
            }
            else {
                block(newCar.getTail(), result.critical, board);
            }
        }
        System.out.println("After");
        for (int i = 0; i < 6; ++i) {
            for (int j = 0; j < 6; ++ j) {
                System.out.print(board[i][j]);
            }
            System.out.println();
        }
        System.out.println();
        return result.critical;
    }

    private Car generatePlayer(int[][] board, int column) {
        Random r = new Random();
        int x = r.nextInt(column - 2) + 1;
        Coordination head = new Coordination(x, 2);
        Coordination tail = new Coordination(x-1, 2);
        board[2][x] = 1;
        board[2][x-1] = 1;
        Car player = new Car(true, "Horizontal", 2);
        player.getPos().add(head);
        player.getPos().add(tail);
        return player;
    }

    private void initMatrix(int col, int row, int[][] board) {
        for (int i = 0; i < row; ++i) {
            for (int j = 0; j < col; ++j) {
                board[i][j] = 0;
            }
        }
    }
}




/*
package whatever.gridlock.model;
import java.lang.System;
import java.util.*;

class NormalGridLock implements Generator<Car> {

    private class Choice {
        String direct;
        String facing;
        int size;
        Set<Coordination> critical;

        Choice(String d, String fac, int size) {
            this.direct = d;
            this.facing = fac;
            this.size = size;
            this.critical = new HashSet<>();
        }
/*
        void print() {
            System.out.println("Direct: " + direct + " Facing: " + facing + " Size: " + size + " Coordination: " + critical.getX() + " " + critical.getY());
        }

    }

    @Override
    public int[][] generate(int col, int row, int[][] board, List<Car> entities) {
        initMatrix(col, row, board);
        Car player = generatePlayer(board, board.length);
        System.out.println("Player: " + player.getHead().getX() + " " + player.getHead().getY());
        entities.add(player);
        //for (int i = player.getHead().getX(); i < 4; i++) {
           // board[2][i] = -1;
        //}
        for (int i = 5; i > 4; i--) {
            //if (board[i][2] == 0) {
                Coordination critical = new Coordination(i, player.getHead().getY());
                gen(critical, board, entities);
            //}
        }
        /*
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (board[i][j] == 0) {
                    Coordination c = new Coordination(j, i);
                    gen(c, c, board, entities);
                }
                //System.out.print(board[i][j]);
            }
            //System.out.println();
        }

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                //if (board[i][j] == -1) {
                    //board[i][j] = 0;
                //}
               System.out.print(board[i][j]);
            }
            System.out.println();
        }
        return board;
    }

    private void gen(Coordination target, int[][] board, List<Car> entities) {
        Set<Coordination> result = generateCar(target, board, entities);
        assert result != null;
        for (Coordination j : result) {
            System.out.print(j.toString() + " ");
        }
        System.out.println();
        while(!result.isEmpty()) {
            Set<Coordination> newLevel = new HashSet<>();
            for (Coordination i : result) {
                if(board[i.getY()][i.getX()] == -1) board[i.getY()][i.getX()] = 0;
                Set<Coordination> x = generateCar(i, board, entities);
                if (x != null) {
                    for (Coordination j : newLevel) {
                        board[j.getY()][j.getX()] = -1;
                    }
                    newLevel.addAll(x);
                }
            }
            for (Coordination j : newLevel) {
                board[j.getY()][j.getX()] = -1;
                System.out.print(j.toString() + " ");
            }
            System.out.println();
            result = newLevel;
        }
        /*
        Set<Coordination> result = generateCar(target, board, entities);
        if (result == null || result.isEmpty()) {
            board[last.getY()][last.getX()] = -1;
            return;
        }
        for (Coordination c : result) {
            //System.out.print(c.getX() + " " + c.getY());
            //generateCar(c, board, entities);
            gen(c, last, board, entities);
        }
        //System.out.println();

    }

    private boolean validate(int[][] board, Coordination target, String facing, int size, String direction) {
        //System.out.println("miao " + board.length + " " + target.getX() + " " + target.getY() + " " + size);
        if (direction.equals("Vertical")) {
            switch (facing) {
                case "Head":
                    return target.getY() + size - 1 < board.length && target.getY() + size < board.length &&
                            board[target.getY() + size - 1][target.getX()] == 0 &&
                            board[target.getY() + size][target.getX()] == 0;
                case "Body":
                    return target.getY() - 1 >= 0 && target.getY() + 1 < board.length &&
                            board[target.getY() + 1][target.getX()] == 0 &&
                            board[target.getY() - 1][target.getX()] == 0;
                default:
                    return target.getY() - size + 1 >= 0 && target.getY() - size >= 0 &&
                            board[target.getY() - size + 1][target.getX()] == 0 &&
                            board[target.getY() - size][target.getX()] == 0;
            }
        }
        else {
            switch (facing) {
                case "Tail":
                    return target.getX() + size - 1 < board.length && target.getX() + size < board.length &&
                            board[target.getY()][target.getX() + size - 1] == 0 &&
                            board[target.getY()][target.getX() + size] == 0;
                case "Body":
                    return target.getX() - 1 >= 0 && target.getX() + 1 < board.length &&
                            board[target.getY()][target.getX() + 1] == 0 &&
                            board[target.getY()][target.getX() - 1] == 0;
                default:
                    return target.getX() - size + 1 >= 0 && target.getX() - size >= 0 &&
                            board[target.getY()][target.getX() - size + 1] == 0 &&
                            board[target.getY()][target.getX() - size] == 0;
            }

        }
    }

    private void block(Coordination from, Coordination to, int[][] board) {
        if (from.getX() == to.getX()) {
            if (from.getY() > to.getY()) {
                for (int i = to.getY(); i <= from.getY(); i++) {
                    board[i][to.getX()] = -1;
                }
            }
            else {
                for (int i = from.getY(); i <= to.getY(); i++) {
                    board[i][to.getX()] = -1;
                }
            }
        }
        else {
            if (from.getX() > to.getX()) {
                for (int i = to.getX(); i <= from.getX(); i++) {
                    board[to.getY()][i] = -1;
                }
            }
            else {
                for (int i = from.getY(); i <= to.getY(); i++) {
                    board[to.getY()][i] = -1;
                }
            }
        }

    }

    private void liftBlock(Coordination from, Coordination to, int[][] board) {
        if (from.getX() == to.getX()) {
            if (from.getY() > to.getY()) {
                for (int i = to.getY(); i <= from.getY(); i++) {
                    board[i][to.getX()] = 0;
                }
            }
            else {
                for (int i = from.getY(); i <= to.getY(); i++) {
                    board[i][to.getX()] = 0;
                }
            }
        }
        else {
            if (from.getX() > to.getX()) {
                for (int i = to.getX(); i <= from.getX(); i++) {
                    board[to.getY()][i] = 0;
                }
            }
            else {
                for (int i = from.getY(); i <= to.getY(); i++) {
                    board[to.getY()][i] = 0;
                }
            }
        }

    }

    private void search(List<Choice> choiceSet, String dir, String facing, int size, Coordination co, int[][]board, Coordination target) {
        if (!validate(board, target, facing, size, dir)) return;
        boolean test = false;
        for(Choice c:choiceSet) {
            if (c.direct.equals(dir) && c.facing.equals(facing)) {
                c.critical.add(co);
                test = true;
            }
        }
        if (!test) {
            Choice c = new Choice(dir, facing, size);
            c.critical.add(co);
            choiceSet.add(c);
        }
    }

    private Set<Coordination> generateCar(Coordination target, int[][] board, List<Car> entities) {
        Random r = new Random();
        List<Choice> choiceSet = new ArrayList<>();
        boolean flag = true;
        String dir = "Vertical";
        for (int i = 2; i <= 3; i++) {
            if (target.getY()-i >= 0 && board[target.getY()-i][target.getX()] == 0) {
                for (int j = target.getY(); j >= 0 && j > target.getY()-i; j--) {
                    if (board[j][target.getX()] != 0) {
                        flag = false;
                    }
                }
            }
            else {
                flag = false;
            }
            if (flag) {
                Coordination critical1 = new Coordination(target.getX(), target.getY()-i);
                search(choiceSet,dir,"Head",i,critical1,board,target);
                search(choiceSet,dir,"Tail",i,critical1,board,target);
            }

            flag = true;
            if (target.getY()+i < board.length && board[target.getY()+i][target.getX()] == 0) {
                for (int j = target.getY(); j < target.getY()+i; j++) {
                    if (board[j][target.getX()] != 0) {
                        flag = false;
                    }
                }
            }
            else {
                flag = false;
            }
            if (flag) {
                Coordination critical = new Coordination(target.getX(), target.getY()+i);
                search(choiceSet,dir,"Head",i,critical,board,target);
                search(choiceSet,dir,"Tail",i,critical,board,target);
            }

            if (i == 3) {
                flag = true;
                if (target.getY() + 2 < board.length && board[target.getY() + 2][target.getX()] == 0) {
                    for (int j = target.getY(); j < target.getY() + 2; j++) {
                        if (board[j][target.getX()] != 0) {
                            flag = false;
                        }
                    }
                } else {
                    flag = false;
                }
                if (flag) {
                    Coordination critical = new Coordination(target.getX(), target.getY() + 2);
                    search(choiceSet, dir, "Body", i, critical, board, target);
                }

                flag = true;
                if (target.getY() - 2 >= 0 && board[target.getY() - 2][target.getX()] == 0) {
                    for (int j = target.getY(); j < target.getY() - 2; j++) {
                        if (board[j][target.getX()] != 0) {
                            flag = false;
                        }
                    }
                } else {
                    flag = false;
                }
                if (flag) {
                    Coordination critical = new Coordination(target.getX(), target.getY() - 2);
                    search(choiceSet, dir, "Body", i, critical, board, target);
                }
            }
        }


        flag = true;
        if (target.getY() != 2) {
            dir = "Horizontal";
            for (int i = 2; i <= 3; ++i) {
                if (target.getX()+i < board[0].length && board[target.getY()][target.getX()+i] == 0) {
                    for (int j = target.getX(); j < target.getX()+i; j++) {
                        if (board[target.getY()][j] != 0) {
                            flag = false;
                        }
                    }
                }
                else {
                    flag = false;
                }
                if (flag) {
                    Coordination critical = new Coordination(target.getX()+i, target.getY());
                    search(choiceSet,dir,"Head",i,critical,board,target);
                    search(choiceSet,dir,"Tail",i,critical,board,target);
                }

                flag = true;
                if (target.getX()-i >= 0 && board[target.getY()][target.getX()-i] == 0) {
                    for (int j = target.getX(); j >= 0 && j > target.getX()-i; j--) {
                        if (board[target.getY()][j] != 0) {
                            flag = false;
                        }
                    }
                }
                else {
                    flag = false;
                }
                if (flag) {
                    Coordination critical = new Coordination(target.getX()-i, target.getY());
                    search(choiceSet,dir,"Head",i,critical,board,target);
                    search(choiceSet,dir,"Tail",i,critical,board,target);
                }

                if (i == 3) {
                    flag = true;
                    if (target.getX() + 2 < board.length && board[target.getY()][target.getX()+2] == 0) {
                        for (int j = target.getX(); j < target.getX() + 2; j++) {
                            if (board[target.getY()][target.getX()+2] != 0) {
                                flag = false;
                            }
                        }
                    } else {
                        flag = false;
                    }
                    if (flag) {
                        Coordination critical = new Coordination(target.getX() + 2, target.getY());
                        search(choiceSet, dir, "Body", i, critical, board, target);
                    }

                    flag = true;
                    if (target.getX() - 2 >= 0 && board[target.getY()][target.getX()-2] == 0) {
                        for (int j = target.getX(); j < target.getX() - 2; j++) {
                            if (board[target.getY()][target.getX()-2] != 0) {
                                flag = false;
                            }
                        }
                    } else {
                        flag = false;
                    }
                    if (flag) {
                        Coordination critical = new Coordination(target.getX() - 2, target.getY());
                        search(choiceSet, dir, "Body", i, critical, board, target);
                    }
                }
            }
        }
        if (choiceSet.size() == 0) return null;
        int index = r.nextInt(choiceSet.size());
        Choice result = choiceSet.get(index);
        /*
        for (Choice i : choiceSet) {
            i.print();
        }

        //System.out.println(target.getX() + " miao " + target.getY());
        Car newCar;
        //System.out.println();
        //result.print();
        //System.out.println();
        Coordination head;
        Coordination tail;
        Coordination body;
        if (result.size == 2) {
            if (result.facing.equals("Head")) {
                head = target;
                if (result.direct.equals("Horizontal")) {
                    tail = new Coordination(target.getX()-1, target.getY());
                    newCar = new Car(false, "Horizontal", 2);
                }
                else {
                    tail = new Coordination(target.getX(), target.getY()+1);
                    newCar = new Car(false, "Vertical", 2);
                }
            }
            else {
                tail = target;
                if (result.direct.equals("Horizontal")) {
                    head = new Coordination(target.getX()+1, target.getY());
                    newCar = new Car(false, "Horizontal", 2);
                }
                else {
                    head = new Coordination(target.getX(), target.getY()-1);
                    newCar = new Car(false, "Vertical", 2);
                }
            }
            newCar.getPos().add(head);
            newCar.getPos().add(tail);
            board[head.getY()][head.getX()] = 1;
            board[tail.getY()][tail.getX()] = 1;
        }
        else {
            switch (result.facing) {
                case "Head":
                    head = target;
                    if (result.direct.equals("Horizontal")) {
                        body = new Coordination(target.getX() - 1, target.getY());
                        tail = new Coordination(target.getX() - 2, target.getY());
                        newCar = new Car(false, "Horizontal", 3);
                    } else {
                        body = new Coordination(target.getX(), target.getY() + 1);
                        tail = new Coordination(target.getX(), target.getY() + 2);
                        newCar = new Car(false, "Vertical", 3);
                    }
                    break;
                case "Body":
                    if (result.direct.equals("Horizontal")) {
                        body = new Coordination(target.getX(), target.getY());
                        head = new Coordination(target.getX() + 1, target.getY());
                        tail = new Coordination(target.getX() - 1, target.getY());
                        newCar = new Car(false, "Horizontal", 3);
                    } else {
                        body = new Coordination(target.getX(), target.getY());
                        head = new Coordination(target.getX(), target.getY() - 1);
                        tail = new Coordination(target.getX(), target.getY() + 1);
                        newCar = new Car(false, "Vertical", 3);
                    }
                    break;
                default:
                    tail = target;
                    if (result.direct.equals("Horizontal")) {
                        body = new Coordination(target.getX() + 1, target.getY());
                        head = new Coordination(target.getX() + 2, target.getY());
                        newCar = new Car(false, "Horizontal", 3);
                    } else {
                        body = new Coordination(target.getX(), target.getY() - 1);
                        head = new Coordination(target.getX(), target.getY() - 2);
                        newCar = new Car(false, "Vertical", 3);
                    }
                    break;
            }
            newCar.getPos().add(head);
            newCar.getPos().add(body);
            newCar.getPos().add(tail);
            board[head.getY()][head.getX()] = 1;
            board[body.getY()][body.getX()] = 1;
            board[tail.getY()][tail.getX()] = 1;
        }
        entities.add(newCar);
        System.out.print("Car ");
        newCar.print();
        /*
        for (Coordination c: result.critical) {
            System.out.println(" to " + "[" + c.getX() + "," + c.getY() + "]");
        }

        return result.critical;
    }

    private Car generatePlayer(int[][] board, int column) {
        Random r = new Random();
        int x = r.nextInt(column - 3) + 1;
        Coordination head = new Coordination(x, 2);
        Coordination tail = new Coordination(x-1, 2);
        board[2][x] = 1;
        board[2][x-1] = 1;
        Car player = new Car(true, "Horizontal", 2);
        player.getPos().add(head);
        player.getPos().add(tail);
        return player;
    }

    private void initMatrix(int col, int row, int[][] board) {
        for (int i = 0; i < row; ++i) {
            for (int j = 0; j < col; ++j) {
                board[i][j] = 0;
            }
        }
    }
}
*/