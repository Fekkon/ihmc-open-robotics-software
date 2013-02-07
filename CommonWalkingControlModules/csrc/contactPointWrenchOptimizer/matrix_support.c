/* Produced by CVXGEN, 2013-02-07 00:01:31 -0500.  */
/* CVXGEN is Copyright (C) 2006-2012 Jacob Mattingley, jem@cvxgen.com. */
/* The code in this file is Copyright (C) 2006-2012 Jacob Mattingley. */
/* CVXGEN, or solvers produced by CVXGEN, cannot be used for commercial */
/* applications without prior written permission from Jacob Mattingley. */

/* Filename: matrix_support.c. */
/* Description: Support functions for matrix multiplication and vector filling. */
#include "solver.h"
void multbymA(double *lhs, double *rhs) {
  lhs[0] = -rhs[0]*(params.A[0])-rhs[1]*(params.A[6])-rhs[2]*(params.A[12])-rhs[3]*(params.A[18])-rhs[4]*(params.A[24])-rhs[5]*(params.A[30])-rhs[6]*(params.A[36])-rhs[7]*(params.A[42])-rhs[8]*(params.A[48])-rhs[9]*(params.A[54])-rhs[10]*(params.A[60])-rhs[11]*(params.A[66])-rhs[12]*(params.A[72])-rhs[13]*(params.A[78])-rhs[14]*(params.A[84])-rhs[15]*(params.A[90])-rhs[16]*(params.A[96])-rhs[17]*(params.A[102])-rhs[18]*(params.A[108])-rhs[19]*(params.A[114])-rhs[20]*(params.A[120])-rhs[21]*(params.A[126])-rhs[22]*(params.A[132])-rhs[23]*(params.A[138])-rhs[24]*(params.A[144])-rhs[25]*(params.A[150])-rhs[26]*(params.A[156])-rhs[27]*(params.A[162])-rhs[28]*(params.A[168])-rhs[29]*(params.A[174])-rhs[30]*(params.A[180])-rhs[31]*(params.A[186])-rhs[32]*(params.A[192])-rhs[33]*(params.A[198])-rhs[34]*(params.A[204])-rhs[35]*(params.A[210])-rhs[36]*(params.A[216])-rhs[37]*(params.A[222])-rhs[38]*(params.A[228])-rhs[39]*(params.A[234])-rhs[40]*(params.A[240])-rhs[41]*(params.A[246])-rhs[42]*(params.A[252])-rhs[43]*(params.A[258])-rhs[44]*(params.A[264])-rhs[45]*(params.A[270])-rhs[46]*(params.A[276])-rhs[47]*(params.A[282])-rhs[48]*(-1);
  lhs[1] = -rhs[0]*(params.A[1])-rhs[1]*(params.A[7])-rhs[2]*(params.A[13])-rhs[3]*(params.A[19])-rhs[4]*(params.A[25])-rhs[5]*(params.A[31])-rhs[6]*(params.A[37])-rhs[7]*(params.A[43])-rhs[8]*(params.A[49])-rhs[9]*(params.A[55])-rhs[10]*(params.A[61])-rhs[11]*(params.A[67])-rhs[12]*(params.A[73])-rhs[13]*(params.A[79])-rhs[14]*(params.A[85])-rhs[15]*(params.A[91])-rhs[16]*(params.A[97])-rhs[17]*(params.A[103])-rhs[18]*(params.A[109])-rhs[19]*(params.A[115])-rhs[20]*(params.A[121])-rhs[21]*(params.A[127])-rhs[22]*(params.A[133])-rhs[23]*(params.A[139])-rhs[24]*(params.A[145])-rhs[25]*(params.A[151])-rhs[26]*(params.A[157])-rhs[27]*(params.A[163])-rhs[28]*(params.A[169])-rhs[29]*(params.A[175])-rhs[30]*(params.A[181])-rhs[31]*(params.A[187])-rhs[32]*(params.A[193])-rhs[33]*(params.A[199])-rhs[34]*(params.A[205])-rhs[35]*(params.A[211])-rhs[36]*(params.A[217])-rhs[37]*(params.A[223])-rhs[38]*(params.A[229])-rhs[39]*(params.A[235])-rhs[40]*(params.A[241])-rhs[41]*(params.A[247])-rhs[42]*(params.A[253])-rhs[43]*(params.A[259])-rhs[44]*(params.A[265])-rhs[45]*(params.A[271])-rhs[46]*(params.A[277])-rhs[47]*(params.A[283])-rhs[49]*(-1);
  lhs[2] = -rhs[0]*(params.A[2])-rhs[1]*(params.A[8])-rhs[2]*(params.A[14])-rhs[3]*(params.A[20])-rhs[4]*(params.A[26])-rhs[5]*(params.A[32])-rhs[6]*(params.A[38])-rhs[7]*(params.A[44])-rhs[8]*(params.A[50])-rhs[9]*(params.A[56])-rhs[10]*(params.A[62])-rhs[11]*(params.A[68])-rhs[12]*(params.A[74])-rhs[13]*(params.A[80])-rhs[14]*(params.A[86])-rhs[15]*(params.A[92])-rhs[16]*(params.A[98])-rhs[17]*(params.A[104])-rhs[18]*(params.A[110])-rhs[19]*(params.A[116])-rhs[20]*(params.A[122])-rhs[21]*(params.A[128])-rhs[22]*(params.A[134])-rhs[23]*(params.A[140])-rhs[24]*(params.A[146])-rhs[25]*(params.A[152])-rhs[26]*(params.A[158])-rhs[27]*(params.A[164])-rhs[28]*(params.A[170])-rhs[29]*(params.A[176])-rhs[30]*(params.A[182])-rhs[31]*(params.A[188])-rhs[32]*(params.A[194])-rhs[33]*(params.A[200])-rhs[34]*(params.A[206])-rhs[35]*(params.A[212])-rhs[36]*(params.A[218])-rhs[37]*(params.A[224])-rhs[38]*(params.A[230])-rhs[39]*(params.A[236])-rhs[40]*(params.A[242])-rhs[41]*(params.A[248])-rhs[42]*(params.A[254])-rhs[43]*(params.A[260])-rhs[44]*(params.A[266])-rhs[45]*(params.A[272])-rhs[46]*(params.A[278])-rhs[47]*(params.A[284])-rhs[50]*(-1);
  lhs[3] = -rhs[0]*(params.A[3])-rhs[1]*(params.A[9])-rhs[2]*(params.A[15])-rhs[3]*(params.A[21])-rhs[4]*(params.A[27])-rhs[5]*(params.A[33])-rhs[6]*(params.A[39])-rhs[7]*(params.A[45])-rhs[8]*(params.A[51])-rhs[9]*(params.A[57])-rhs[10]*(params.A[63])-rhs[11]*(params.A[69])-rhs[12]*(params.A[75])-rhs[13]*(params.A[81])-rhs[14]*(params.A[87])-rhs[15]*(params.A[93])-rhs[16]*(params.A[99])-rhs[17]*(params.A[105])-rhs[18]*(params.A[111])-rhs[19]*(params.A[117])-rhs[20]*(params.A[123])-rhs[21]*(params.A[129])-rhs[22]*(params.A[135])-rhs[23]*(params.A[141])-rhs[24]*(params.A[147])-rhs[25]*(params.A[153])-rhs[26]*(params.A[159])-rhs[27]*(params.A[165])-rhs[28]*(params.A[171])-rhs[29]*(params.A[177])-rhs[30]*(params.A[183])-rhs[31]*(params.A[189])-rhs[32]*(params.A[195])-rhs[33]*(params.A[201])-rhs[34]*(params.A[207])-rhs[35]*(params.A[213])-rhs[36]*(params.A[219])-rhs[37]*(params.A[225])-rhs[38]*(params.A[231])-rhs[39]*(params.A[237])-rhs[40]*(params.A[243])-rhs[41]*(params.A[249])-rhs[42]*(params.A[255])-rhs[43]*(params.A[261])-rhs[44]*(params.A[267])-rhs[45]*(params.A[273])-rhs[46]*(params.A[279])-rhs[47]*(params.A[285])-rhs[51]*(-1);
  lhs[4] = -rhs[0]*(params.A[4])-rhs[1]*(params.A[10])-rhs[2]*(params.A[16])-rhs[3]*(params.A[22])-rhs[4]*(params.A[28])-rhs[5]*(params.A[34])-rhs[6]*(params.A[40])-rhs[7]*(params.A[46])-rhs[8]*(params.A[52])-rhs[9]*(params.A[58])-rhs[10]*(params.A[64])-rhs[11]*(params.A[70])-rhs[12]*(params.A[76])-rhs[13]*(params.A[82])-rhs[14]*(params.A[88])-rhs[15]*(params.A[94])-rhs[16]*(params.A[100])-rhs[17]*(params.A[106])-rhs[18]*(params.A[112])-rhs[19]*(params.A[118])-rhs[20]*(params.A[124])-rhs[21]*(params.A[130])-rhs[22]*(params.A[136])-rhs[23]*(params.A[142])-rhs[24]*(params.A[148])-rhs[25]*(params.A[154])-rhs[26]*(params.A[160])-rhs[27]*(params.A[166])-rhs[28]*(params.A[172])-rhs[29]*(params.A[178])-rhs[30]*(params.A[184])-rhs[31]*(params.A[190])-rhs[32]*(params.A[196])-rhs[33]*(params.A[202])-rhs[34]*(params.A[208])-rhs[35]*(params.A[214])-rhs[36]*(params.A[220])-rhs[37]*(params.A[226])-rhs[38]*(params.A[232])-rhs[39]*(params.A[238])-rhs[40]*(params.A[244])-rhs[41]*(params.A[250])-rhs[42]*(params.A[256])-rhs[43]*(params.A[262])-rhs[44]*(params.A[268])-rhs[45]*(params.A[274])-rhs[46]*(params.A[280])-rhs[47]*(params.A[286])-rhs[52]*(-1);
  lhs[5] = -rhs[0]*(params.A[5])-rhs[1]*(params.A[11])-rhs[2]*(params.A[17])-rhs[3]*(params.A[23])-rhs[4]*(params.A[29])-rhs[5]*(params.A[35])-rhs[6]*(params.A[41])-rhs[7]*(params.A[47])-rhs[8]*(params.A[53])-rhs[9]*(params.A[59])-rhs[10]*(params.A[65])-rhs[11]*(params.A[71])-rhs[12]*(params.A[77])-rhs[13]*(params.A[83])-rhs[14]*(params.A[89])-rhs[15]*(params.A[95])-rhs[16]*(params.A[101])-rhs[17]*(params.A[107])-rhs[18]*(params.A[113])-rhs[19]*(params.A[119])-rhs[20]*(params.A[125])-rhs[21]*(params.A[131])-rhs[22]*(params.A[137])-rhs[23]*(params.A[143])-rhs[24]*(params.A[149])-rhs[25]*(params.A[155])-rhs[26]*(params.A[161])-rhs[27]*(params.A[167])-rhs[28]*(params.A[173])-rhs[29]*(params.A[179])-rhs[30]*(params.A[185])-rhs[31]*(params.A[191])-rhs[32]*(params.A[197])-rhs[33]*(params.A[203])-rhs[34]*(params.A[209])-rhs[35]*(params.A[215])-rhs[36]*(params.A[221])-rhs[37]*(params.A[227])-rhs[38]*(params.A[233])-rhs[39]*(params.A[239])-rhs[40]*(params.A[245])-rhs[41]*(params.A[251])-rhs[42]*(params.A[257])-rhs[43]*(params.A[263])-rhs[44]*(params.A[269])-rhs[45]*(params.A[275])-rhs[46]*(params.A[281])-rhs[47]*(params.A[287])-rhs[53]*(-1);
}
void multbymAT(double *lhs, double *rhs) {
  lhs[0] = -rhs[0]*(params.A[0])-rhs[1]*(params.A[1])-rhs[2]*(params.A[2])-rhs[3]*(params.A[3])-rhs[4]*(params.A[4])-rhs[5]*(params.A[5]);
  lhs[1] = -rhs[0]*(params.A[6])-rhs[1]*(params.A[7])-rhs[2]*(params.A[8])-rhs[3]*(params.A[9])-rhs[4]*(params.A[10])-rhs[5]*(params.A[11]);
  lhs[2] = -rhs[0]*(params.A[12])-rhs[1]*(params.A[13])-rhs[2]*(params.A[14])-rhs[3]*(params.A[15])-rhs[4]*(params.A[16])-rhs[5]*(params.A[17]);
  lhs[3] = -rhs[0]*(params.A[18])-rhs[1]*(params.A[19])-rhs[2]*(params.A[20])-rhs[3]*(params.A[21])-rhs[4]*(params.A[22])-rhs[5]*(params.A[23]);
  lhs[4] = -rhs[0]*(params.A[24])-rhs[1]*(params.A[25])-rhs[2]*(params.A[26])-rhs[3]*(params.A[27])-rhs[4]*(params.A[28])-rhs[5]*(params.A[29]);
  lhs[5] = -rhs[0]*(params.A[30])-rhs[1]*(params.A[31])-rhs[2]*(params.A[32])-rhs[3]*(params.A[33])-rhs[4]*(params.A[34])-rhs[5]*(params.A[35]);
  lhs[6] = -rhs[0]*(params.A[36])-rhs[1]*(params.A[37])-rhs[2]*(params.A[38])-rhs[3]*(params.A[39])-rhs[4]*(params.A[40])-rhs[5]*(params.A[41]);
  lhs[7] = -rhs[0]*(params.A[42])-rhs[1]*(params.A[43])-rhs[2]*(params.A[44])-rhs[3]*(params.A[45])-rhs[4]*(params.A[46])-rhs[5]*(params.A[47]);
  lhs[8] = -rhs[0]*(params.A[48])-rhs[1]*(params.A[49])-rhs[2]*(params.A[50])-rhs[3]*(params.A[51])-rhs[4]*(params.A[52])-rhs[5]*(params.A[53]);
  lhs[9] = -rhs[0]*(params.A[54])-rhs[1]*(params.A[55])-rhs[2]*(params.A[56])-rhs[3]*(params.A[57])-rhs[4]*(params.A[58])-rhs[5]*(params.A[59]);
  lhs[10] = -rhs[0]*(params.A[60])-rhs[1]*(params.A[61])-rhs[2]*(params.A[62])-rhs[3]*(params.A[63])-rhs[4]*(params.A[64])-rhs[5]*(params.A[65]);
  lhs[11] = -rhs[0]*(params.A[66])-rhs[1]*(params.A[67])-rhs[2]*(params.A[68])-rhs[3]*(params.A[69])-rhs[4]*(params.A[70])-rhs[5]*(params.A[71]);
  lhs[12] = -rhs[0]*(params.A[72])-rhs[1]*(params.A[73])-rhs[2]*(params.A[74])-rhs[3]*(params.A[75])-rhs[4]*(params.A[76])-rhs[5]*(params.A[77]);
  lhs[13] = -rhs[0]*(params.A[78])-rhs[1]*(params.A[79])-rhs[2]*(params.A[80])-rhs[3]*(params.A[81])-rhs[4]*(params.A[82])-rhs[5]*(params.A[83]);
  lhs[14] = -rhs[0]*(params.A[84])-rhs[1]*(params.A[85])-rhs[2]*(params.A[86])-rhs[3]*(params.A[87])-rhs[4]*(params.A[88])-rhs[5]*(params.A[89]);
  lhs[15] = -rhs[0]*(params.A[90])-rhs[1]*(params.A[91])-rhs[2]*(params.A[92])-rhs[3]*(params.A[93])-rhs[4]*(params.A[94])-rhs[5]*(params.A[95]);
  lhs[16] = -rhs[0]*(params.A[96])-rhs[1]*(params.A[97])-rhs[2]*(params.A[98])-rhs[3]*(params.A[99])-rhs[4]*(params.A[100])-rhs[5]*(params.A[101]);
  lhs[17] = -rhs[0]*(params.A[102])-rhs[1]*(params.A[103])-rhs[2]*(params.A[104])-rhs[3]*(params.A[105])-rhs[4]*(params.A[106])-rhs[5]*(params.A[107]);
  lhs[18] = -rhs[0]*(params.A[108])-rhs[1]*(params.A[109])-rhs[2]*(params.A[110])-rhs[3]*(params.A[111])-rhs[4]*(params.A[112])-rhs[5]*(params.A[113]);
  lhs[19] = -rhs[0]*(params.A[114])-rhs[1]*(params.A[115])-rhs[2]*(params.A[116])-rhs[3]*(params.A[117])-rhs[4]*(params.A[118])-rhs[5]*(params.A[119]);
  lhs[20] = -rhs[0]*(params.A[120])-rhs[1]*(params.A[121])-rhs[2]*(params.A[122])-rhs[3]*(params.A[123])-rhs[4]*(params.A[124])-rhs[5]*(params.A[125]);
  lhs[21] = -rhs[0]*(params.A[126])-rhs[1]*(params.A[127])-rhs[2]*(params.A[128])-rhs[3]*(params.A[129])-rhs[4]*(params.A[130])-rhs[5]*(params.A[131]);
  lhs[22] = -rhs[0]*(params.A[132])-rhs[1]*(params.A[133])-rhs[2]*(params.A[134])-rhs[3]*(params.A[135])-rhs[4]*(params.A[136])-rhs[5]*(params.A[137]);
  lhs[23] = -rhs[0]*(params.A[138])-rhs[1]*(params.A[139])-rhs[2]*(params.A[140])-rhs[3]*(params.A[141])-rhs[4]*(params.A[142])-rhs[5]*(params.A[143]);
  lhs[24] = -rhs[0]*(params.A[144])-rhs[1]*(params.A[145])-rhs[2]*(params.A[146])-rhs[3]*(params.A[147])-rhs[4]*(params.A[148])-rhs[5]*(params.A[149]);
  lhs[25] = -rhs[0]*(params.A[150])-rhs[1]*(params.A[151])-rhs[2]*(params.A[152])-rhs[3]*(params.A[153])-rhs[4]*(params.A[154])-rhs[5]*(params.A[155]);
  lhs[26] = -rhs[0]*(params.A[156])-rhs[1]*(params.A[157])-rhs[2]*(params.A[158])-rhs[3]*(params.A[159])-rhs[4]*(params.A[160])-rhs[5]*(params.A[161]);
  lhs[27] = -rhs[0]*(params.A[162])-rhs[1]*(params.A[163])-rhs[2]*(params.A[164])-rhs[3]*(params.A[165])-rhs[4]*(params.A[166])-rhs[5]*(params.A[167]);
  lhs[28] = -rhs[0]*(params.A[168])-rhs[1]*(params.A[169])-rhs[2]*(params.A[170])-rhs[3]*(params.A[171])-rhs[4]*(params.A[172])-rhs[5]*(params.A[173]);
  lhs[29] = -rhs[0]*(params.A[174])-rhs[1]*(params.A[175])-rhs[2]*(params.A[176])-rhs[3]*(params.A[177])-rhs[4]*(params.A[178])-rhs[5]*(params.A[179]);
  lhs[30] = -rhs[0]*(params.A[180])-rhs[1]*(params.A[181])-rhs[2]*(params.A[182])-rhs[3]*(params.A[183])-rhs[4]*(params.A[184])-rhs[5]*(params.A[185]);
  lhs[31] = -rhs[0]*(params.A[186])-rhs[1]*(params.A[187])-rhs[2]*(params.A[188])-rhs[3]*(params.A[189])-rhs[4]*(params.A[190])-rhs[5]*(params.A[191]);
  lhs[32] = -rhs[0]*(params.A[192])-rhs[1]*(params.A[193])-rhs[2]*(params.A[194])-rhs[3]*(params.A[195])-rhs[4]*(params.A[196])-rhs[5]*(params.A[197]);
  lhs[33] = -rhs[0]*(params.A[198])-rhs[1]*(params.A[199])-rhs[2]*(params.A[200])-rhs[3]*(params.A[201])-rhs[4]*(params.A[202])-rhs[5]*(params.A[203]);
  lhs[34] = -rhs[0]*(params.A[204])-rhs[1]*(params.A[205])-rhs[2]*(params.A[206])-rhs[3]*(params.A[207])-rhs[4]*(params.A[208])-rhs[5]*(params.A[209]);
  lhs[35] = -rhs[0]*(params.A[210])-rhs[1]*(params.A[211])-rhs[2]*(params.A[212])-rhs[3]*(params.A[213])-rhs[4]*(params.A[214])-rhs[5]*(params.A[215]);
  lhs[36] = -rhs[0]*(params.A[216])-rhs[1]*(params.A[217])-rhs[2]*(params.A[218])-rhs[3]*(params.A[219])-rhs[4]*(params.A[220])-rhs[5]*(params.A[221]);
  lhs[37] = -rhs[0]*(params.A[222])-rhs[1]*(params.A[223])-rhs[2]*(params.A[224])-rhs[3]*(params.A[225])-rhs[4]*(params.A[226])-rhs[5]*(params.A[227]);
  lhs[38] = -rhs[0]*(params.A[228])-rhs[1]*(params.A[229])-rhs[2]*(params.A[230])-rhs[3]*(params.A[231])-rhs[4]*(params.A[232])-rhs[5]*(params.A[233]);
  lhs[39] = -rhs[0]*(params.A[234])-rhs[1]*(params.A[235])-rhs[2]*(params.A[236])-rhs[3]*(params.A[237])-rhs[4]*(params.A[238])-rhs[5]*(params.A[239]);
  lhs[40] = -rhs[0]*(params.A[240])-rhs[1]*(params.A[241])-rhs[2]*(params.A[242])-rhs[3]*(params.A[243])-rhs[4]*(params.A[244])-rhs[5]*(params.A[245]);
  lhs[41] = -rhs[0]*(params.A[246])-rhs[1]*(params.A[247])-rhs[2]*(params.A[248])-rhs[3]*(params.A[249])-rhs[4]*(params.A[250])-rhs[5]*(params.A[251]);
  lhs[42] = -rhs[0]*(params.A[252])-rhs[1]*(params.A[253])-rhs[2]*(params.A[254])-rhs[3]*(params.A[255])-rhs[4]*(params.A[256])-rhs[5]*(params.A[257]);
  lhs[43] = -rhs[0]*(params.A[258])-rhs[1]*(params.A[259])-rhs[2]*(params.A[260])-rhs[3]*(params.A[261])-rhs[4]*(params.A[262])-rhs[5]*(params.A[263]);
  lhs[44] = -rhs[0]*(params.A[264])-rhs[1]*(params.A[265])-rhs[2]*(params.A[266])-rhs[3]*(params.A[267])-rhs[4]*(params.A[268])-rhs[5]*(params.A[269]);
  lhs[45] = -rhs[0]*(params.A[270])-rhs[1]*(params.A[271])-rhs[2]*(params.A[272])-rhs[3]*(params.A[273])-rhs[4]*(params.A[274])-rhs[5]*(params.A[275]);
  lhs[46] = -rhs[0]*(params.A[276])-rhs[1]*(params.A[277])-rhs[2]*(params.A[278])-rhs[3]*(params.A[279])-rhs[4]*(params.A[280])-rhs[5]*(params.A[281]);
  lhs[47] = -rhs[0]*(params.A[282])-rhs[1]*(params.A[283])-rhs[2]*(params.A[284])-rhs[3]*(params.A[285])-rhs[4]*(params.A[286])-rhs[5]*(params.A[287]);
  lhs[48] = -rhs[0]*(-1);
  lhs[49] = -rhs[1]*(-1);
  lhs[50] = -rhs[2]*(-1);
  lhs[51] = -rhs[3]*(-1);
  lhs[52] = -rhs[4]*(-1);
  lhs[53] = -rhs[5]*(-1);
}
void multbymG(double *lhs, double *rhs) {
  lhs[0] = -rhs[0]*(-1);
  lhs[1] = -rhs[1]*(-1);
  lhs[2] = -rhs[2]*(-1);
  lhs[3] = -rhs[3]*(-1);
  lhs[4] = -rhs[4]*(-1);
  lhs[5] = -rhs[5]*(-1);
  lhs[6] = -rhs[6]*(-1);
  lhs[7] = -rhs[7]*(-1);
  lhs[8] = -rhs[8]*(-1);
  lhs[9] = -rhs[9]*(-1);
  lhs[10] = -rhs[10]*(-1);
  lhs[11] = -rhs[11]*(-1);
  lhs[12] = -rhs[12]*(-1);
  lhs[13] = -rhs[13]*(-1);
  lhs[14] = -rhs[14]*(-1);
  lhs[15] = -rhs[15]*(-1);
  lhs[16] = -rhs[16]*(-1);
  lhs[17] = -rhs[17]*(-1);
  lhs[18] = -rhs[18]*(-1);
  lhs[19] = -rhs[19]*(-1);
  lhs[20] = -rhs[20]*(-1);
  lhs[21] = -rhs[21]*(-1);
  lhs[22] = -rhs[22]*(-1);
  lhs[23] = -rhs[23]*(-1);
  lhs[24] = -rhs[24]*(-1);
  lhs[25] = -rhs[25]*(-1);
  lhs[26] = -rhs[26]*(-1);
  lhs[27] = -rhs[27]*(-1);
  lhs[28] = -rhs[28]*(-1);
  lhs[29] = -rhs[29]*(-1);
  lhs[30] = -rhs[30]*(-1);
  lhs[31] = -rhs[31]*(-1);
  lhs[32] = -rhs[32]*(-1);
  lhs[33] = -rhs[33]*(-1);
  lhs[34] = -rhs[34]*(-1);
  lhs[35] = -rhs[35]*(-1);
  lhs[36] = -rhs[36]*(-1);
  lhs[37] = -rhs[37]*(-1);
  lhs[38] = -rhs[38]*(-1);
  lhs[39] = -rhs[39]*(-1);
  lhs[40] = -rhs[40]*(-1);
  lhs[41] = -rhs[41]*(-1);
  lhs[42] = -rhs[42]*(-1);
  lhs[43] = -rhs[43]*(-1);
  lhs[44] = -rhs[44]*(-1);
  lhs[45] = -rhs[45]*(-1);
  lhs[46] = -rhs[46]*(-1);
  lhs[47] = -rhs[47]*(-1);
  lhs[48] = -rhs[0]*(-params.B[0])-rhs[1]*(-params.B[3])-rhs[2]*(-params.B[6])-rhs[3]*(-params.B[9])-rhs[4]*(-params.B[12])-rhs[5]*(-params.B[15])-rhs[6]*(-params.B[18])-rhs[7]*(-params.B[21])-rhs[8]*(-params.B[24])-rhs[9]*(-params.B[27])-rhs[10]*(-params.B[30])-rhs[11]*(-params.B[33])-rhs[12]*(-params.B[36])-rhs[13]*(-params.B[39])-rhs[14]*(-params.B[42])-rhs[15]*(-params.B[45])-rhs[16]*(-params.B[48])-rhs[17]*(-params.B[51])-rhs[18]*(-params.B[54])-rhs[19]*(-params.B[57])-rhs[20]*(-params.B[60])-rhs[21]*(-params.B[63])-rhs[22]*(-params.B[66])-rhs[23]*(-params.B[69])-rhs[24]*(-params.B[72])-rhs[25]*(-params.B[75])-rhs[26]*(-params.B[78])-rhs[27]*(-params.B[81])-rhs[28]*(-params.B[84])-rhs[29]*(-params.B[87])-rhs[30]*(-params.B[90])-rhs[31]*(-params.B[93])-rhs[32]*(-params.B[96])-rhs[33]*(-params.B[99])-rhs[34]*(-params.B[102])-rhs[35]*(-params.B[105])-rhs[36]*(-params.B[108])-rhs[37]*(-params.B[111])-rhs[38]*(-params.B[114])-rhs[39]*(-params.B[117])-rhs[40]*(-params.B[120])-rhs[41]*(-params.B[123])-rhs[42]*(-params.B[126])-rhs[43]*(-params.B[129])-rhs[44]*(-params.B[132])-rhs[45]*(-params.B[135])-rhs[46]*(-params.B[138])-rhs[47]*(-params.B[141]);
  lhs[49] = -rhs[0]*(-params.B[1])-rhs[1]*(-params.B[4])-rhs[2]*(-params.B[7])-rhs[3]*(-params.B[10])-rhs[4]*(-params.B[13])-rhs[5]*(-params.B[16])-rhs[6]*(-params.B[19])-rhs[7]*(-params.B[22])-rhs[8]*(-params.B[25])-rhs[9]*(-params.B[28])-rhs[10]*(-params.B[31])-rhs[11]*(-params.B[34])-rhs[12]*(-params.B[37])-rhs[13]*(-params.B[40])-rhs[14]*(-params.B[43])-rhs[15]*(-params.B[46])-rhs[16]*(-params.B[49])-rhs[17]*(-params.B[52])-rhs[18]*(-params.B[55])-rhs[19]*(-params.B[58])-rhs[20]*(-params.B[61])-rhs[21]*(-params.B[64])-rhs[22]*(-params.B[67])-rhs[23]*(-params.B[70])-rhs[24]*(-params.B[73])-rhs[25]*(-params.B[76])-rhs[26]*(-params.B[79])-rhs[27]*(-params.B[82])-rhs[28]*(-params.B[85])-rhs[29]*(-params.B[88])-rhs[30]*(-params.B[91])-rhs[31]*(-params.B[94])-rhs[32]*(-params.B[97])-rhs[33]*(-params.B[100])-rhs[34]*(-params.B[103])-rhs[35]*(-params.B[106])-rhs[36]*(-params.B[109])-rhs[37]*(-params.B[112])-rhs[38]*(-params.B[115])-rhs[39]*(-params.B[118])-rhs[40]*(-params.B[121])-rhs[41]*(-params.B[124])-rhs[42]*(-params.B[127])-rhs[43]*(-params.B[130])-rhs[44]*(-params.B[133])-rhs[45]*(-params.B[136])-rhs[46]*(-params.B[139])-rhs[47]*(-params.B[142]);
  lhs[50] = -rhs[0]*(-params.B[2])-rhs[1]*(-params.B[5])-rhs[2]*(-params.B[8])-rhs[3]*(-params.B[11])-rhs[4]*(-params.B[14])-rhs[5]*(-params.B[17])-rhs[6]*(-params.B[20])-rhs[7]*(-params.B[23])-rhs[8]*(-params.B[26])-rhs[9]*(-params.B[29])-rhs[10]*(-params.B[32])-rhs[11]*(-params.B[35])-rhs[12]*(-params.B[38])-rhs[13]*(-params.B[41])-rhs[14]*(-params.B[44])-rhs[15]*(-params.B[47])-rhs[16]*(-params.B[50])-rhs[17]*(-params.B[53])-rhs[18]*(-params.B[56])-rhs[19]*(-params.B[59])-rhs[20]*(-params.B[62])-rhs[21]*(-params.B[65])-rhs[22]*(-params.B[68])-rhs[23]*(-params.B[71])-rhs[24]*(-params.B[74])-rhs[25]*(-params.B[77])-rhs[26]*(-params.B[80])-rhs[27]*(-params.B[83])-rhs[28]*(-params.B[86])-rhs[29]*(-params.B[89])-rhs[30]*(-params.B[92])-rhs[31]*(-params.B[95])-rhs[32]*(-params.B[98])-rhs[33]*(-params.B[101])-rhs[34]*(-params.B[104])-rhs[35]*(-params.B[107])-rhs[36]*(-params.B[110])-rhs[37]*(-params.B[113])-rhs[38]*(-params.B[116])-rhs[39]*(-params.B[119])-rhs[40]*(-params.B[122])-rhs[41]*(-params.B[125])-rhs[42]*(-params.B[128])-rhs[43]*(-params.B[131])-rhs[44]*(-params.B[134])-rhs[45]*(-params.B[137])-rhs[46]*(-params.B[140])-rhs[47]*(-params.B[143]);
}
void multbymGT(double *lhs, double *rhs) {
  lhs[0] = -rhs[0]*(-1)-rhs[48]*(-params.B[0])-rhs[49]*(-params.B[1])-rhs[50]*(-params.B[2]);
  lhs[1] = -rhs[1]*(-1)-rhs[48]*(-params.B[3])-rhs[49]*(-params.B[4])-rhs[50]*(-params.B[5]);
  lhs[2] = -rhs[2]*(-1)-rhs[48]*(-params.B[6])-rhs[49]*(-params.B[7])-rhs[50]*(-params.B[8]);
  lhs[3] = -rhs[3]*(-1)-rhs[48]*(-params.B[9])-rhs[49]*(-params.B[10])-rhs[50]*(-params.B[11]);
  lhs[4] = -rhs[4]*(-1)-rhs[48]*(-params.B[12])-rhs[49]*(-params.B[13])-rhs[50]*(-params.B[14]);
  lhs[5] = -rhs[5]*(-1)-rhs[48]*(-params.B[15])-rhs[49]*(-params.B[16])-rhs[50]*(-params.B[17]);
  lhs[6] = -rhs[6]*(-1)-rhs[48]*(-params.B[18])-rhs[49]*(-params.B[19])-rhs[50]*(-params.B[20]);
  lhs[7] = -rhs[7]*(-1)-rhs[48]*(-params.B[21])-rhs[49]*(-params.B[22])-rhs[50]*(-params.B[23]);
  lhs[8] = -rhs[8]*(-1)-rhs[48]*(-params.B[24])-rhs[49]*(-params.B[25])-rhs[50]*(-params.B[26]);
  lhs[9] = -rhs[9]*(-1)-rhs[48]*(-params.B[27])-rhs[49]*(-params.B[28])-rhs[50]*(-params.B[29]);
  lhs[10] = -rhs[10]*(-1)-rhs[48]*(-params.B[30])-rhs[49]*(-params.B[31])-rhs[50]*(-params.B[32]);
  lhs[11] = -rhs[11]*(-1)-rhs[48]*(-params.B[33])-rhs[49]*(-params.B[34])-rhs[50]*(-params.B[35]);
  lhs[12] = -rhs[12]*(-1)-rhs[48]*(-params.B[36])-rhs[49]*(-params.B[37])-rhs[50]*(-params.B[38]);
  lhs[13] = -rhs[13]*(-1)-rhs[48]*(-params.B[39])-rhs[49]*(-params.B[40])-rhs[50]*(-params.B[41]);
  lhs[14] = -rhs[14]*(-1)-rhs[48]*(-params.B[42])-rhs[49]*(-params.B[43])-rhs[50]*(-params.B[44]);
  lhs[15] = -rhs[15]*(-1)-rhs[48]*(-params.B[45])-rhs[49]*(-params.B[46])-rhs[50]*(-params.B[47]);
  lhs[16] = -rhs[16]*(-1)-rhs[48]*(-params.B[48])-rhs[49]*(-params.B[49])-rhs[50]*(-params.B[50]);
  lhs[17] = -rhs[17]*(-1)-rhs[48]*(-params.B[51])-rhs[49]*(-params.B[52])-rhs[50]*(-params.B[53]);
  lhs[18] = -rhs[18]*(-1)-rhs[48]*(-params.B[54])-rhs[49]*(-params.B[55])-rhs[50]*(-params.B[56]);
  lhs[19] = -rhs[19]*(-1)-rhs[48]*(-params.B[57])-rhs[49]*(-params.B[58])-rhs[50]*(-params.B[59]);
  lhs[20] = -rhs[20]*(-1)-rhs[48]*(-params.B[60])-rhs[49]*(-params.B[61])-rhs[50]*(-params.B[62]);
  lhs[21] = -rhs[21]*(-1)-rhs[48]*(-params.B[63])-rhs[49]*(-params.B[64])-rhs[50]*(-params.B[65]);
  lhs[22] = -rhs[22]*(-1)-rhs[48]*(-params.B[66])-rhs[49]*(-params.B[67])-rhs[50]*(-params.B[68]);
  lhs[23] = -rhs[23]*(-1)-rhs[48]*(-params.B[69])-rhs[49]*(-params.B[70])-rhs[50]*(-params.B[71]);
  lhs[24] = -rhs[24]*(-1)-rhs[48]*(-params.B[72])-rhs[49]*(-params.B[73])-rhs[50]*(-params.B[74]);
  lhs[25] = -rhs[25]*(-1)-rhs[48]*(-params.B[75])-rhs[49]*(-params.B[76])-rhs[50]*(-params.B[77]);
  lhs[26] = -rhs[26]*(-1)-rhs[48]*(-params.B[78])-rhs[49]*(-params.B[79])-rhs[50]*(-params.B[80]);
  lhs[27] = -rhs[27]*(-1)-rhs[48]*(-params.B[81])-rhs[49]*(-params.B[82])-rhs[50]*(-params.B[83]);
  lhs[28] = -rhs[28]*(-1)-rhs[48]*(-params.B[84])-rhs[49]*(-params.B[85])-rhs[50]*(-params.B[86]);
  lhs[29] = -rhs[29]*(-1)-rhs[48]*(-params.B[87])-rhs[49]*(-params.B[88])-rhs[50]*(-params.B[89]);
  lhs[30] = -rhs[30]*(-1)-rhs[48]*(-params.B[90])-rhs[49]*(-params.B[91])-rhs[50]*(-params.B[92]);
  lhs[31] = -rhs[31]*(-1)-rhs[48]*(-params.B[93])-rhs[49]*(-params.B[94])-rhs[50]*(-params.B[95]);
  lhs[32] = -rhs[32]*(-1)-rhs[48]*(-params.B[96])-rhs[49]*(-params.B[97])-rhs[50]*(-params.B[98]);
  lhs[33] = -rhs[33]*(-1)-rhs[48]*(-params.B[99])-rhs[49]*(-params.B[100])-rhs[50]*(-params.B[101]);
  lhs[34] = -rhs[34]*(-1)-rhs[48]*(-params.B[102])-rhs[49]*(-params.B[103])-rhs[50]*(-params.B[104]);
  lhs[35] = -rhs[35]*(-1)-rhs[48]*(-params.B[105])-rhs[49]*(-params.B[106])-rhs[50]*(-params.B[107]);
  lhs[36] = -rhs[36]*(-1)-rhs[48]*(-params.B[108])-rhs[49]*(-params.B[109])-rhs[50]*(-params.B[110]);
  lhs[37] = -rhs[37]*(-1)-rhs[48]*(-params.B[111])-rhs[49]*(-params.B[112])-rhs[50]*(-params.B[113]);
  lhs[38] = -rhs[38]*(-1)-rhs[48]*(-params.B[114])-rhs[49]*(-params.B[115])-rhs[50]*(-params.B[116]);
  lhs[39] = -rhs[39]*(-1)-rhs[48]*(-params.B[117])-rhs[49]*(-params.B[118])-rhs[50]*(-params.B[119]);
  lhs[40] = -rhs[40]*(-1)-rhs[48]*(-params.B[120])-rhs[49]*(-params.B[121])-rhs[50]*(-params.B[122]);
  lhs[41] = -rhs[41]*(-1)-rhs[48]*(-params.B[123])-rhs[49]*(-params.B[124])-rhs[50]*(-params.B[125]);
  lhs[42] = -rhs[42]*(-1)-rhs[48]*(-params.B[126])-rhs[49]*(-params.B[127])-rhs[50]*(-params.B[128]);
  lhs[43] = -rhs[43]*(-1)-rhs[48]*(-params.B[129])-rhs[49]*(-params.B[130])-rhs[50]*(-params.B[131]);
  lhs[44] = -rhs[44]*(-1)-rhs[48]*(-params.B[132])-rhs[49]*(-params.B[133])-rhs[50]*(-params.B[134]);
  lhs[45] = -rhs[45]*(-1)-rhs[48]*(-params.B[135])-rhs[49]*(-params.B[136])-rhs[50]*(-params.B[137]);
  lhs[46] = -rhs[46]*(-1)-rhs[48]*(-params.B[138])-rhs[49]*(-params.B[139])-rhs[50]*(-params.B[140]);
  lhs[47] = -rhs[47]*(-1)-rhs[48]*(-params.B[141])-rhs[49]*(-params.B[142])-rhs[50]*(-params.B[143]);
  lhs[48] = 0;
  lhs[49] = 0;
  lhs[50] = 0;
  lhs[51] = 0;
  lhs[52] = 0;
  lhs[53] = 0;
}
void multbyP(double *lhs, double *rhs) {
  /* TODO use the fact that P is symmetric? */
  /* TODO check doubling / half factor etc. */
  lhs[0] = rhs[0]*(2*params.epsilon[0]);
  lhs[1] = rhs[1]*(2*params.epsilon[0]);
  lhs[2] = rhs[2]*(2*params.epsilon[0]);
  lhs[3] = rhs[3]*(2*params.epsilon[0]);
  lhs[4] = rhs[4]*(2*params.epsilon[0]);
  lhs[5] = rhs[5]*(2*params.epsilon[0]);
  lhs[6] = rhs[6]*(2*params.epsilon[0]);
  lhs[7] = rhs[7]*(2*params.epsilon[0]);
  lhs[8] = rhs[8]*(2*params.epsilon[0]);
  lhs[9] = rhs[9]*(2*params.epsilon[0]);
  lhs[10] = rhs[10]*(2*params.epsilon[0]);
  lhs[11] = rhs[11]*(2*params.epsilon[0]);
  lhs[12] = rhs[12]*(2*params.epsilon[0]);
  lhs[13] = rhs[13]*(2*params.epsilon[0]);
  lhs[14] = rhs[14]*(2*params.epsilon[0]);
  lhs[15] = rhs[15]*(2*params.epsilon[0]);
  lhs[16] = rhs[16]*(2*params.epsilon[0]);
  lhs[17] = rhs[17]*(2*params.epsilon[0]);
  lhs[18] = rhs[18]*(2*params.epsilon[0]);
  lhs[19] = rhs[19]*(2*params.epsilon[0]);
  lhs[20] = rhs[20]*(2*params.epsilon[0]);
  lhs[21] = rhs[21]*(2*params.epsilon[0]);
  lhs[22] = rhs[22]*(2*params.epsilon[0]);
  lhs[23] = rhs[23]*(2*params.epsilon[0]);
  lhs[24] = rhs[24]*(2*params.epsilon[0]);
  lhs[25] = rhs[25]*(2*params.epsilon[0]);
  lhs[26] = rhs[26]*(2*params.epsilon[0]);
  lhs[27] = rhs[27]*(2*params.epsilon[0]);
  lhs[28] = rhs[28]*(2*params.epsilon[0]);
  lhs[29] = rhs[29]*(2*params.epsilon[0]);
  lhs[30] = rhs[30]*(2*params.epsilon[0]);
  lhs[31] = rhs[31]*(2*params.epsilon[0]);
  lhs[32] = rhs[32]*(2*params.epsilon[0]);
  lhs[33] = rhs[33]*(2*params.epsilon[0]);
  lhs[34] = rhs[34]*(2*params.epsilon[0]);
  lhs[35] = rhs[35]*(2*params.epsilon[0]);
  lhs[36] = rhs[36]*(2*params.epsilon[0]);
  lhs[37] = rhs[37]*(2*params.epsilon[0]);
  lhs[38] = rhs[38]*(2*params.epsilon[0]);
  lhs[39] = rhs[39]*(2*params.epsilon[0]);
  lhs[40] = rhs[40]*(2*params.epsilon[0]);
  lhs[41] = rhs[41]*(2*params.epsilon[0]);
  lhs[42] = rhs[42]*(2*params.epsilon[0]);
  lhs[43] = rhs[43]*(2*params.epsilon[0]);
  lhs[44] = rhs[44]*(2*params.epsilon[0]);
  lhs[45] = rhs[45]*(2*params.epsilon[0]);
  lhs[46] = rhs[46]*(2*params.epsilon[0]);
  lhs[47] = rhs[47]*(2*params.epsilon[0]);
  lhs[48] = rhs[48]*(2*params.C[0]);
  lhs[49] = rhs[49]*(2*params.C[1]);
  lhs[50] = rhs[50]*(2*params.C[2]);
  lhs[51] = rhs[51]*(2*params.C[3]);
  lhs[52] = rhs[52]*(2*params.C[4]);
  lhs[53] = rhs[53]*(2*params.C[5]);
}
void fillq(void) {
  work.q[0] = 0;
  work.q[1] = 0;
  work.q[2] = 0;
  work.q[3] = 0;
  work.q[4] = 0;
  work.q[5] = 0;
  work.q[6] = 0;
  work.q[7] = 0;
  work.q[8] = 0;
  work.q[9] = 0;
  work.q[10] = 0;
  work.q[11] = 0;
  work.q[12] = 0;
  work.q[13] = 0;
  work.q[14] = 0;
  work.q[15] = 0;
  work.q[16] = 0;
  work.q[17] = 0;
  work.q[18] = 0;
  work.q[19] = 0;
  work.q[20] = 0;
  work.q[21] = 0;
  work.q[22] = 0;
  work.q[23] = 0;
  work.q[24] = 0;
  work.q[25] = 0;
  work.q[26] = 0;
  work.q[27] = 0;
  work.q[28] = 0;
  work.q[29] = 0;
  work.q[30] = 0;
  work.q[31] = 0;
  work.q[32] = 0;
  work.q[33] = 0;
  work.q[34] = 0;
  work.q[35] = 0;
  work.q[36] = 0;
  work.q[37] = 0;
  work.q[38] = 0;
  work.q[39] = 0;
  work.q[40] = 0;
  work.q[41] = 0;
  work.q[42] = 0;
  work.q[43] = 0;
  work.q[44] = 0;
  work.q[45] = 0;
  work.q[46] = 0;
  work.q[47] = 0;
  work.q[48] = 0;
  work.q[49] = 0;
  work.q[50] = 0;
  work.q[51] = 0;
  work.q[52] = 0;
  work.q[53] = 0;
}
void fillh(void) {
  work.h[0] = 0;
  work.h[1] = 0;
  work.h[2] = 0;
  work.h[3] = 0;
  work.h[4] = 0;
  work.h[5] = 0;
  work.h[6] = 0;
  work.h[7] = 0;
  work.h[8] = 0;
  work.h[9] = 0;
  work.h[10] = 0;
  work.h[11] = 0;
  work.h[12] = 0;
  work.h[13] = 0;
  work.h[14] = 0;
  work.h[15] = 0;
  work.h[16] = 0;
  work.h[17] = 0;
  work.h[18] = 0;
  work.h[19] = 0;
  work.h[20] = 0;
  work.h[21] = 0;
  work.h[22] = 0;
  work.h[23] = 0;
  work.h[24] = 0;
  work.h[25] = 0;
  work.h[26] = 0;
  work.h[27] = 0;
  work.h[28] = 0;
  work.h[29] = 0;
  work.h[30] = 0;
  work.h[31] = 0;
  work.h[32] = 0;
  work.h[33] = 0;
  work.h[34] = 0;
  work.h[35] = 0;
  work.h[36] = 0;
  work.h[37] = 0;
  work.h[38] = 0;
  work.h[39] = 0;
  work.h[40] = 0;
  work.h[41] = 0;
  work.h[42] = 0;
  work.h[43] = 0;
  work.h[44] = 0;
  work.h[45] = 0;
  work.h[46] = 0;
  work.h[47] = 0;
  work.h[48] = -params.fmin[0];
  work.h[49] = -params.fmin[1];
  work.h[50] = -params.fmin[2];
}
void fillb(void) {
  work.b[0] = params.W[0];
  work.b[1] = params.W[1];
  work.b[2] = params.W[2];
  work.b[3] = params.W[3];
  work.b[4] = params.W[4];
  work.b[5] = params.W[5];
}
void pre_ops(void) {
}
