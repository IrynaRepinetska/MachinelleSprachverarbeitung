public class MergeSort {
    static void sort(Token[] a, int l)          // entry function
    {
        if(l < 2)                    // if size < 2 return
            return;
        Token[] b = new Token[l];
        BottomUpMergeSort(a, b, l);
    }

    static void BottomUpMergeSort(Token[] a, Token[] b, int l)
    {
    int n = l;
    int s = 1;                              // run size 
        if(1 == (GetPassCount(n)&1)){       // if odd number of passes
            for(s = 1; s < n; s += 2)       // swap in place for 1st pass
                if(a[s].count < a[s-1].count){
                    Token t = a[s];
                    a[s] = a[s-1];
                    a[s-1] = t;
                }
            s = 2;
        }
        while(s < n){                       // while not done
            int ee = 0;                     // reset end index
            while(ee < n){                  // merge pairs of runs
                int ll = ee;                // ll = start of left  run
                int rr = ll+s;              // rr = start of right run
                if(rr >= n){                // if only left run
                    do                      //   copy it
                        b[ll] = a[ll];
                    while(++ll < n);
                    break;                  //   end of pass
                }
                ee = rr+s;                  // ee = end of right run
                if(ee > n)
                    ee = n;
                Merge(a, b, ll, rr, ee);
            }
            {                               // swap references
                Token[] t = a;
                a = b;
                b = t;
            }
            s <<= 1;                        // double the run size
        }
    }

    static void Merge(Token[] a, Token[] b, int ll, int rr, int ee) {
        int o = ll;                         // b[]       index
        int l = ll;                         // a[] left  index
        int r = rr;                         // a[] right index
        while(true){                        // merge data
            if(a[l].count <= a[r].count){               // if a[l] <= a[r]
                b[o++] = a[l++];            //   copy a[l]
                if(l < rr)                  //   if not end of left run
                    continue;               //     continue (back to while)
                do                          //   else copy rest of right run
                    b[o++] = a[r++];
                while(r < ee);
                break;                      //     and return
            } else {                        // else a[l] > a[r]
                b[o++] = a[r++];            //   copy a[r]
                if(r < ee)                  //   if not end of right run
                    continue;               //     continue (back to while)
                do                          //   else copy rest of left run
                    b[o++] = a[l++];
                while(l < rr);
                break;                      //     and return
            }
        }
    }

    static int GetPassCount(int n)          // return # passes
    {
        int i = 0;
        for(int s = 1; s < n; s <<= 1)
            i += 1;
        return(i);
    }
}