/*
 * Copyright 2013-2021 Phokham Nonava
 *
 * Use of this source code is governed by the MIT license that can be
 * found in the LICENSE file.
 */
package com.fluxchess.pulse;

import com.fluxchess.jcpi.models.GenericBoard;
import com.fluxchess.jcpi.models.GenericMove;
import com.fluxchess.pulse.model.Color;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import static com.fluxchess.pulse.MoveList.MoveEntry;

class MoveGeneratorTest {

	private static final P[] perftPositions = {
			p("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", e(1, 20), e(2, 400), e(3, 8902), e(4, 197281), e(5, 4865609), e(6, 119060324)),
			p("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1", e(1, 48), e(2, 2039), e(3, 97862), e(4, 4085603), e(5, 193690690)),
			p("4k3/8/8/8/8/8/8/4K2R w K - 0 1", e(1, 15), e(2, 66), e(3, 1197), e(4, 7059), e(5, 133987), e(6, 764643)),
			p("4k3/8/8/8/8/8/8/R3K3 w Q - 0 1", e(1, 16), e(2, 71), e(3, 1287), e(4, 7626), e(5, 145232), e(6, 846648)),
			p("4k2r/8/8/8/8/8/8/4K3 w k - 0 1", e(1, 5), e(2, 75), e(3, 459), e(4, 8290), e(5, 47635), e(6, 899442)),
			p("r3k3/8/8/8/8/8/8/4K3 w q - 0 1", e(1, 5), e(2, 80), e(3, 493), e(4, 8897), e(5, 52710), e(6, 1001523)),
			p("4k3/8/8/8/8/8/8/R3K2R w KQ - 0 1", e(1, 26), e(2, 112), e(3, 3189), e(4, 17945), e(5, 532933), e(6, 2788982)),
			p("r3k2r/8/8/8/8/8/8/4K3 w kq - 0 1", e(1, 5), e(2, 130), e(3, 782), e(4, 22180), e(5, 118882), e(6, 3517770)),
			p("8/8/8/8/8/8/6k1/4K2R w K - 0 1", e(1, 12), e(2, 38), e(3, 564), e(4, 2219), e(5, 37735), e(6, 185867)),
			p("8/8/8/8/8/8/1k6/R3K3 w Q - 0 1", e(1, 15), e(2, 65), e(3, 1018), e(4, 4573), e(5, 80619), e(6, 413018)),
			p("4k2r/6K1/8/8/8/8/8/8 w k - 0 1", e(1, 3), e(2, 32), e(3, 134), e(4, 2073), e(5, 10485), e(6, 179869)),
			p("r3k3/1K6/8/8/8/8/8/8 w q - 0 1", e(1, 4), e(2, 49), e(3, 243), e(4, 3991), e(5, 20780), e(6, 367724)),
			p("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1", e(1, 26), e(2, 568), e(3, 13744), e(4, 314346), e(5, 7594526), e(6, 179862938)),
			p("r3k2r/8/8/8/8/8/8/1R2K2R w Kkq - 0 1", e(1, 25), e(2, 567), e(3, 14095), e(4, 328965), e(5, 8153719), e(6, 195629489)),
			p("r3k2r/8/8/8/8/8/8/2R1K2R w Kkq - 0 1", e(1, 25), e(2, 548), e(3, 13502), e(4, 312835), e(5, 7736373), e(6, 184411439)),
			p("r3k2r/8/8/8/8/8/8/R3K1R1 w Qkq - 0 1", e(1, 25), e(2, 547), e(3, 13579), e(4, 316214), e(5, 7878456), e(6, 189224276)),
			p("1r2k2r/8/8/8/8/8/8/R3K2R w KQk - 0 1", e(1, 26), e(2, 583), e(3, 14252), e(4, 334705), e(5, 8198901), e(6, 198328929)),
			p("2r1k2r/8/8/8/8/8/8/R3K2R w KQk - 0 1", e(1, 25), e(2, 560), e(3, 13592), e(4, 317324), e(5, 7710115), e(6, 185959088)),
			p("r3k1r1/8/8/8/8/8/8/R3K2R w KQq - 0 1", e(1, 25), e(2, 560), e(3, 13607), e(4, 320792), e(5, 7848606), e(6, 190755813)),
			p("4k3/8/8/8/8/8/8/4K2R b K - 0 1", e(1, 5), e(2, 75), e(3, 459), e(4, 8290), e(5, 47635), e(6, 899442)),
			p("4k3/8/8/8/8/8/8/R3K3 b Q - 0 1", e(1, 5), e(2, 80), e(3, 493), e(4, 8897), e(5, 52710), e(6, 1001523)),
			p("4k2r/8/8/8/8/8/8/4K3 b k - 0 1", e(1, 15), e(2, 66), e(3, 1197), e(4, 7059), e(5, 133987), e(6, 764643)),
			p("r3k3/8/8/8/8/8/8/4K3 b q - 0 1", e(1, 16), e(2, 71), e(3, 1287), e(4, 7626), e(5, 145232), e(6, 846648)),
			p("4k3/8/8/8/8/8/8/R3K2R b KQ - 0 1", e(1, 5), e(2, 130), e(3, 782), e(4, 22180), e(5, 118882), e(6, 3517770)),
			p("r3k2r/8/8/8/8/8/8/4K3 b kq - 0 1", e(1, 26), e(2, 112), e(3, 3189), e(4, 17945), e(5, 532933), e(6, 2788982)),
			p("8/8/8/8/8/8/6k1/4K2R b K - 0 1", e(1, 3), e(2, 32), e(3, 134), e(4, 2073), e(5, 10485), e(6, 179869)),
			p("8/8/8/8/8/8/1k6/R3K3 b Q - 0 1", e(1, 4), e(2, 49), e(3, 243), e(4, 3991), e(5, 20780), e(6, 367724)),
			p("4k2r/6K1/8/8/8/8/8/8 b k - 0 1", e(1, 12), e(2, 38), e(3, 564), e(4, 2219), e(5, 37735), e(6, 185867)),
			p("r3k3/1K6/8/8/8/8/8/8 b q - 0 1", e(1, 15), e(2, 65), e(3, 1018), e(4, 4573), e(5, 80619), e(6, 413018)),
			p("r3k2r/8/8/8/8/8/8/R3K2R b KQkq - 0 1", e(1, 26), e(2, 568), e(3, 13744), e(4, 314346), e(5, 7594526), e(6, 179862938)),
			p("r3k2r/8/8/8/8/8/8/1R2K2R b Kkq - 0 1", e(1, 26), e(2, 583), e(3, 14252), e(4, 334705), e(5, 8198901), e(6, 198328929)),
			p("r3k2r/8/8/8/8/8/8/2R1K2R b Kkq - 0 1", e(1, 25), e(2, 560), e(3, 13592), e(4, 317324), e(5, 7710115), e(6, 185959088)),
			p("r3k2r/8/8/8/8/8/8/R3K1R1 b Qkq - 0 1", e(1, 25), e(2, 560), e(3, 13607), e(4, 320792), e(5, 7848606), e(6, 190755813)),
			p("1r2k2r/8/8/8/8/8/8/R3K2R b KQk - 0 1", e(1, 25), e(2, 567), e(3, 14095), e(4, 328965), e(5, 8153719), e(6, 195629489)),
			p("2r1k2r/8/8/8/8/8/8/R3K2R b KQk - 0 1", e(1, 25), e(2, 548), e(3, 13502), e(4, 312835), e(5, 7736373), e(6, 184411439)),
			p("r3k1r1/8/8/8/8/8/8/R3K2R b KQq - 0 1", e(1, 25), e(2, 547), e(3, 13579), e(4, 316214), e(5, 7878456), e(6, 189224276)),
			p("8/1n4N1/2k5/8/8/5K2/1N4n1/8 w - - 0 1", e(1, 14), e(2, 195), e(3, 2760), e(4, 38675), e(5, 570726), e(6, 8107539)),
			p("8/1k6/8/5N2/8/4n3/8/2K5 w - - 0 1", e(1, 11), e(2, 156), e(3, 1636), e(4, 20534), e(5, 223507), e(6, 2594412)),
			p("8/8/4k3/3Nn3/3nN3/4K3/8/8 w - - 0 1", e(1, 19), e(2, 289), e(3, 4442), e(4, 73584), e(5, 1198299), e(6, 19870403)),
			p("K7/8/2n5/1n6/8/8/8/k6N w - - 0 1", e(1, 3), e(2, 51), e(3, 345), e(4, 5301), e(5, 38348), e(6, 588695)),
			p("k7/8/2N5/1N6/8/8/8/K6n w - - 0 1", e(1, 17), e(2, 54), e(3, 835), e(4, 5910), e(5, 92250), e(6, 688780)),
			p("8/1n4N1/2k5/8/8/5K2/1N4n1/8 b - - 0 1", e(1, 15), e(2, 193), e(3, 2816), e(4, 40039), e(5, 582642), e(6, 8503277)),
			p("8/1k6/8/5N2/8/4n3/8/2K5 b - - 0 1", e(1, 16), e(2, 180), e(3, 2290), e(4, 24640), e(5, 288141), e(6, 3147566)),
			p("8/8/3K4/3Nn3/3nN3/4k3/8/8 b - - 0 1", e(1, 4), e(2, 68), e(3, 1118), e(4, 16199), e(5, 281190), e(6, 4405103)),
			p("K7/8/2n5/1n6/8/8/8/k6N b - - 0 1", e(1, 17), e(2, 54), e(3, 835), e(4, 5910), e(5, 92250), e(6, 688780)),
			p("k7/8/2N5/1N6/8/8/8/K6n b - - 0 1", e(1, 3), e(2, 51), e(3, 345), e(4, 5301), e(5, 38348), e(6, 588695)),
			p("B6b/8/8/8/2K5/4k3/8/b6B w - - 0 1", e(1, 17), e(2, 278), e(3, 4607), e(4, 76778), e(5, 1320507), e(6, 22823890)),
			p("8/8/1B6/7b/7k/8/2B1b3/7K w - - 0 1", e(1, 21), e(2, 316), e(3, 5744), e(4, 93338), e(5, 1713368), e(6, 28861171)),
			p("k7/B7/1B6/1B6/8/8/8/K6b w - - 0 1", e(1, 21), e(2, 144), e(3, 3242), e(4, 32955), e(5, 787524), e(6, 7881673)),
			p("K7/b7/1b6/1b6/8/8/8/k6B w - - 0 1", e(1, 7), e(2, 143), e(3, 1416), e(4, 31787), e(5, 310862), e(6, 7382896)),
			p("B6b/8/8/8/2K5/5k2/8/b6B b - - 0 1", e(1, 6), e(2, 106), e(3, 1829), e(4, 31151), e(5, 530585), e(6, 9250746)),
			p("8/8/1B6/7b/7k/8/2B1b3/7K b - - 0 1", e(1, 17), e(2, 309), e(3, 5133), e(4, 93603), e(5, 1591064), e(6, 29027891)),
			p("k7/B7/1B6/1B6/8/8/8/K6b b - - 0 1", e(1, 7), e(2, 143), e(3, 1416), e(4, 31787), e(5, 310862), e(6, 7382896)),
			p("K7/b7/1b6/1b6/8/8/8/k6B b - - 0 1", e(1, 21), e(2, 144), e(3, 3242), e(4, 32955), e(5, 787524), e(6, 7881673)),
			p("7k/RR6/8/8/8/8/rr6/7K w - - 0 1", e(1, 19), e(2, 275), e(3, 5300), e(4, 104342), e(5, 2161211), e(6, 44956585)),
			p("R6r/8/8/2K5/5k2/8/8/r6R w - - 0 1", e(1, 36), e(2, 1027), e(3, 29215), e(4, 771461), e(5, 20506480), e(6, 525169084)),
			p("7k/RR6/8/8/8/8/rr6/7K b - - 0 1", e(1, 19), e(2, 275), e(3, 5300), e(4, 104342), e(5, 2161211), e(6, 44956585)),
			p("R6r/8/8/2K5/5k2/8/8/r6R b - - 0 1", e(1, 36), e(2, 1027), e(3, 29227), e(4, 771368), e(5, 20521342), e(6, 524966748)),
			p("6kq/8/8/8/8/8/8/7K w - - 0 1", e(1, 2), e(2, 36), e(3, 143), e(4, 3637), e(5, 14893), e(6, 391507)),
			p("K7/8/8/3Q4/4q3/8/8/7k w - - 0 1", e(1, 6), e(2, 35), e(3, 495), e(4, 8349), e(5, 166741), e(6, 3370175)),
			p("6qk/8/8/8/8/8/8/7K b - - 0 1", e(1, 22), e(2, 43), e(3, 1015), e(4, 4167), e(5, 105749), e(6, 419369)),
			p("6KQ/8/8/8/8/8/8/7k b - - 0 1", e(1, 2), e(2, 36), e(3, 143), e(4, 3637), e(5, 14893), e(6, 391507)),
			p("K7/8/8/3Q4/4q3/8/8/7k b - - 0 1", e(1, 6), e(2, 35), e(3, 495), e(4, 8349), e(5, 166741), e(6, 3370175)),
			p("8/8/8/8/8/K7/P7/k7 w - - 0 1", e(1, 3), e(2, 7), e(3, 43), e(4, 199), e(5, 1347), e(6, 6249)),
			p("8/8/8/8/8/7K/7P/7k w - - 0 1", e(1, 3), e(2, 7), e(3, 43), e(4, 199), e(5, 1347), e(6, 6249)),
			p("K7/p7/k7/8/8/8/8/8 w - - 0 1", e(1, 1), e(2, 3), e(3, 12), e(4, 80), e(5, 342), e(6, 2343)),
			p("7K/7p/7k/8/8/8/8/8 w - - 0 1", e(1, 1), e(2, 3), e(3, 12), e(4, 80), e(5, 342), e(6, 2343)),
			p("8/2k1p3/3pP3/3P2K1/8/8/8/8 w - - 0 1", e(1, 7), e(2, 35), e(3, 210), e(4, 1091), e(5, 7028), e(6, 34834)),
			p("8/8/8/8/8/K7/P7/k7 b - - 0 1", e(1, 1), e(2, 3), e(3, 12), e(4, 80), e(5, 342), e(6, 2343)),
			p("8/8/8/8/8/7K/7P/7k b - - 0 1", e(1, 1), e(2, 3), e(3, 12), e(4, 80), e(5, 342), e(6, 2343)),
			p("K7/p7/k7/8/8/8/8/8 b - - 0 1", e(1, 3), e(2, 7), e(3, 43), e(4, 199), e(5, 1347), e(6, 6249)),
			p("7K/7p/7k/8/8/8/8/8 b - - 0 1", e(1, 3), e(2, 7), e(3, 43), e(4, 199), e(5, 1347), e(6, 6249)),
			p("8/2k1p3/3pP3/3P2K1/8/8/8/8 b - - 0 1", e(1, 5), e(2, 35), e(3, 182), e(4, 1091), e(5, 5408), e(6, 34822)),
			p("8/8/8/8/8/4k3/4P3/4K3 w - - 0 1", e(1, 2), e(2, 8), e(3, 44), e(4, 282), e(5, 1814), e(6, 11848)),
			p("4k3/4p3/4K3/8/8/8/8/8 b - - 0 1", e(1, 2), e(2, 8), e(3, 44), e(4, 282), e(5, 1814), e(6, 11848)),
			p("8/8/7k/7p/7P/7K/8/8 w - - 0 1", e(1, 3), e(2, 9), e(3, 57), e(4, 360), e(5, 1969), e(6, 10724)),
			p("8/8/k7/p7/P7/K7/8/8 w - - 0 1", e(1, 3), e(2, 9), e(3, 57), e(4, 360), e(5, 1969), e(6, 10724)),
			p("8/8/3k4/3p4/3P4/3K4/8/8 w - - 0 1", e(1, 5), e(2, 25), e(3, 180), e(4, 1294), e(5, 8296), e(6, 53138)),
			p("8/3k4/3p4/8/3P4/3K4/8/8 w - - 0 1", e(1, 8), e(2, 61), e(3, 483), e(4, 3213), e(5, 23599), e(6, 157093)),
			p("8/8/3k4/3p4/8/3P4/3K4/8 w - - 0 1", e(1, 8), e(2, 61), e(3, 411), e(4, 3213), e(5, 21637), e(6, 158065)),
			p("k7/8/3p4/8/3P4/8/8/7K w - - 0 1", e(1, 4), e(2, 15), e(3, 90), e(4, 534), e(5, 3450), e(6, 20960)),
			p("8/8/7k/7p/7P/7K/8/8 b - - 0 1", e(1, 3), e(2, 9), e(3, 57), e(4, 360), e(5, 1969), e(6, 10724)),
			p("8/8/k7/p7/P7/K7/8/8 b - - 0 1", e(1, 3), e(2, 9), e(3, 57), e(4, 360), e(5, 1969), e(6, 10724)),
			p("8/8/3k4/3p4/3P4/3K4/8/8 b - - 0 1", e(1, 5), e(2, 25), e(3, 180), e(4, 1294), e(5, 8296), e(6, 53138)),
			p("8/3k4/3p4/8/3P4/3K4/8/8 b - - 0 1", e(1, 8), e(2, 61), e(3, 411), e(4, 3213), e(5, 21637), e(6, 158065)),
			p("8/8/3k4/3p4/8/3P4/3K4/8 b - - 0 1", e(1, 8), e(2, 61), e(3, 483), e(4, 3213), e(5, 23599), e(6, 157093)),
			p("k7/8/3p4/8/3P4/8/8/7K b - - 0 1", e(1, 4), e(2, 15), e(3, 89), e(4, 537), e(5, 3309), e(6, 21104)),
			p("7k/3p4/8/8/3P4/8/8/K7 w - - 0 1", e(1, 4), e(2, 19), e(3, 117), e(4, 720), e(5, 4661), e(6, 32191)),
			p("7k/8/8/3p4/8/8/3P4/K7 w - - 0 1", e(1, 5), e(2, 19), e(3, 116), e(4, 716), e(5, 4786), e(6, 30980)),
			p("k7/8/8/7p/6P1/8/8/K7 w - - 0 1", e(1, 5), e(2, 22), e(3, 139), e(4, 877), e(5, 6112), e(6, 41874)),
			p("k7/8/7p/8/8/6P1/8/K7 w - - 0 1", e(1, 4), e(2, 16), e(3, 101), e(4, 637), e(5, 4354), e(6, 29679)),
			p("k7/8/8/6p1/7P/8/8/K7 w - - 0 1", e(1, 5), e(2, 22), e(3, 139), e(4, 877), e(5, 6112), e(6, 41874)),
			p("k7/8/6p1/8/8/7P/8/K7 w - - 0 1", e(1, 4), e(2, 16), e(3, 101), e(4, 637), e(5, 4354), e(6, 29679)),
			p("k7/8/8/3p4/4p3/8/8/7K w - - 0 1", e(1, 3), e(2, 15), e(3, 84), e(4, 573), e(5, 3013), e(6, 22886)),
			p("k7/8/3p4/8/8/4P3/8/7K w - - 0 1", e(1, 4), e(2, 16), e(3, 101), e(4, 637), e(5, 4271), e(6, 28662)),
			p("7k/3p4/8/8/3P4/8/8/K7 b - - 0 1", e(1, 5), e(2, 19), e(3, 117), e(4, 720), e(5, 5014), e(6, 32167)),
			p("7k/8/8/3p4/8/8/3P4/K7 b - - 0 1", e(1, 4), e(2, 19), e(3, 117), e(4, 712), e(5, 4658), e(6, 30749)),
			p("k7/8/8/7p/6P1/8/8/K7 b - - 0 1", e(1, 5), e(2, 22), e(3, 139), e(4, 877), e(5, 6112), e(6, 41874)),
			p("k7/8/7p/8/8/6P1/8/K7 b - - 0 1", e(1, 4), e(2, 16), e(3, 101), e(4, 637), e(5, 4354), e(6, 29679)),
			p("k7/8/8/6p1/7P/8/8/K7 b - - 0 1", e(1, 5), e(2, 22), e(3, 139), e(4, 877), e(5, 6112), e(6, 41874)),
			p("k7/8/6p1/8/8/7P/8/K7 b - - 0 1", e(1, 4), e(2, 16), e(3, 101), e(4, 637), e(5, 4354), e(6, 29679)),
			p("k7/8/8/3p4/4p3/8/8/7K b - - 0 1", e(1, 5), e(2, 15), e(3, 102), e(4, 569), e(5, 4337), e(6, 22579)),
			p("k7/8/3p4/8/8/4P3/8/7K b - - 0 1", e(1, 4), e(2, 16), e(3, 101), e(4, 637), e(5, 4271), e(6, 28662)),
			p("7k/8/8/p7/1P6/8/8/7K w - - 0 1", e(1, 5), e(2, 22), e(3, 139), e(4, 877), e(5, 6112), e(6, 41874)),
			p("7k/8/p7/8/8/1P6/8/7K w - - 0 1", e(1, 4), e(2, 16), e(3, 101), e(4, 637), e(5, 4354), e(6, 29679)),
			p("7k/8/8/1p6/P7/8/8/7K w - - 0 1", e(1, 5), e(2, 22), e(3, 139), e(4, 877), e(5, 6112), e(6, 41874)),
			p("7k/8/1p6/8/8/P7/8/7K w - - 0 1", e(1, 4), e(2, 16), e(3, 101), e(4, 637), e(5, 4354), e(6, 29679)),
			p("k7/7p/8/8/8/8/6P1/K7 w - - 0 1", e(1, 5), e(2, 25), e(3, 161), e(4, 1035), e(5, 7574), e(6, 55338)),
			p("k7/6p1/8/8/8/8/7P/K7 w - - 0 1", e(1, 5), e(2, 25), e(3, 161), e(4, 1035), e(5, 7574), e(6, 55338)),
			p("3k4/3pp3/8/8/8/8/3PP3/3K4 w - - 0 1", e(1, 7), e(2, 49), e(3, 378), e(4, 2902), e(5, 24122), e(6, 199002)),
			p("7k/8/8/p7/1P6/8/8/7K b - - 0 1", e(1, 5), e(2, 22), e(3, 139), e(4, 877), e(5, 6112), e(6, 41874)),
			p("7k/8/p7/8/8/1P6/8/7K b - - 0 1", e(1, 4), e(2, 16), e(3, 101), e(4, 637), e(5, 4354), e(6, 29679)),
			p("7k/8/8/1p6/P7/8/8/7K b - - 0 1", e(1, 5), e(2, 22), e(3, 139), e(4, 877), e(5, 6112), e(6, 41874)),
			p("7k/8/1p6/8/8/P7/8/7K b - - 0 1", e(1, 4), e(2, 16), e(3, 101), e(4, 637), e(5, 4354), e(6, 29679)),
			p("k7/7p/8/8/8/8/6P1/K7 b - - 0 1", e(1, 5), e(2, 25), e(3, 161), e(4, 1035), e(5, 7574), e(6, 55338)),
			p("k7/6p1/8/8/8/8/7P/K7 b - - 0 1", e(1, 5), e(2, 25), e(3, 161), e(4, 1035), e(5, 7574), e(6, 55338)),
			p("3k4/3pp3/8/8/8/8/3PP3/3K4 b - - 0 1", e(1, 7), e(2, 49), e(3, 378), e(4, 2902), e(5, 24122), e(6, 199002)),
			p("8/Pk6/8/8/8/8/6Kp/8 w - - 0 1", e(1, 11), e(2, 97), e(3, 887), e(4, 8048), e(5, 90606), e(6, 1030499)),
			p("n1n5/1Pk5/8/8/8/8/5Kp1/5N1N w - - 0 1", e(1, 24), e(2, 421), e(3, 7421), e(4, 124608), e(5, 2193768), e(6, 37665329)),
			p("8/PPPk4/8/8/8/8/4Kppp/8 w - - 0 1", e(1, 18), e(2, 270), e(3, 4699), e(4, 79355), e(5, 1533145), e(6, 28859283)),
			p("n1n5/PPPk4/8/8/8/8/4Kppp/5N1N w - - 0 1", e(1, 24), e(2, 496), e(3, 9483), e(4, 182838), e(5, 3605103), e(6, 71179139)),
			p("8/Pk6/8/8/8/8/6Kp/8 b - - 0 1", e(1, 11), e(2, 97), e(3, 887), e(4, 8048), e(5, 90606), e(6, 1030499)),
			p("n1n5/1Pk5/8/8/8/8/5Kp1/5N1N b - - 0 1", e(1, 24), e(2, 421), e(3, 7421), e(4, 124608), e(5, 2193768), e(6, 37665329)),
			p("8/PPPk4/8/8/8/8/4Kppp/8 b - - 0 1", e(1, 18), e(2, 270), e(3, 4699), e(4, 79355), e(5, 1533145), e(6, 28859283)),
			p("n1n5/PPPk4/8/8/8/8/4Kppp/5N1N b - - 0 1", e(1, 24), e(2, 496), e(3, 9483), e(4, 182838), e(5, 3605103), e(6, 71179139)),
			p("1k6/8/8/5pP1/4K1P1/8/8/8 w - f6 0 1", e(1, 10), e(2, 63), e(3, 533), e(4, 3508), e(5, 30821))
	};

	private static final int MAX_DEPTH = 6;
	private static final MoveGenerator[] moveGenerators = new MoveGenerator[MAX_DEPTH];

	private static final class P {

		private final String fen;
		private final E[] perftEntries;

		private P(String fen, E... perftEntries) {
			this.fen = fen;
			this.perftEntries = perftEntries;
		}
	}

	private static final class E {

		private final int depth;
		private final long nodes;

		E(int depth, long nodes) {
			this.depth = depth;
			this.nodes = nodes;
		}
	}

	@BeforeAll
	static void setUpClass() {
		for (int i = 0; i < MAX_DEPTH; i++) {
			moveGenerators[i] = new MoveGenerator();
		}
	}

	private static P p(String fen, E... perftEntries) {
		return new P(fen, perftEntries);
	}

	private static E e(int depth, long nodes) {
		return new E(depth, nodes);
	}

	private long miniMax(int depth, Position position, int ply) {
		if (depth <= 0) {
			return 1;
		}

		long totalNodes = 0;

		boolean isCheck = position.isCheck();
		MoveList<MoveEntry> moves = moveGenerators[ply].getMoves(position, depth, isCheck);

		for (int i = 0; i < moves.size; i++) {
			int move = moves.entries[i].move;

			position.makeMove(move);
			if (!position.isCheck(Color.opposite(position.activeColor))) {
				totalNodes += miniMax(depth - 1, position, ply + 1);
			}
			position.undoMove(move);
		}

		return totalNodes;
	}

	@Test
	void testPerft() {
		for (int i = 0; i < 4; i++) {
			for (P p : perftPositions) {
				if (p.perftEntries.length > i) {
					int depth = p.perftEntries[i].depth;
					long nodes = p.perftEntries[i].nodes;

					Position position = Notation.toPosition(p.fen);

					long result = miniMax(depth, position, 0);
					if (nodes != result) {
						throw new AssertionError(findMissingMoves(depth, position, 0));
					}
				}
			}
		}
	}

	private String findMissingMoves(int depth, Position position, int ply) {
		StringBuilder message = new StringBuilder();

		// Get expected moves from JCPI
		GenericBoard genericBoard = Notation.toGenericBoard(position);
		Collection<GenericMove> expectedMoves = new HashSet<>(Arrays.asList(
				com.fluxchess.jcpi.utils.MoveGenerator.getGenericMoves(genericBoard)
		));

		// Get actual moves
		boolean isCheck = position.isCheck();
		MoveList<MoveEntry> moves = moveGenerators[ply].getLegalMoves(position, depth, isCheck);
		Collection<GenericMove> actualMoves = new HashSet<>();
		for (int i = 0; i < moves.size; i++) {
			actualMoves.add(Pulse.fromMove(moves.entries[i].move));
		}

		// Compare expected and actual moves
		Collection<GenericMove> invalidMoves = new HashSet<>(actualMoves);
		invalidMoves.removeAll(expectedMoves);

		Collection<GenericMove> missingMoves = new HashSet<>(expectedMoves);
		missingMoves.removeAll(actualMoves);

		if (invalidMoves.isEmpty() && missingMoves.isEmpty()) {
			if (depth <= 1) {
				return message.toString();
			}

			for (int i = 0; i < moves.size; i++) {
				int move = moves.entries[i].move;

				position.makeMove(move);
				message.append(findMissingMoves(depth - 1, position, ply + 1));
				position.undoMove(move);

				if (message.length() != 0) {
					break;
				}
			}
		} else {
			message.append(String.format("Failed check for board: %s%n", genericBoard));
			message.append(String.format("Expected: %s%n", expectedMoves));
			message.append(String.format("  Actual: %s%n", actualMoves));
			message.append(String.format(" Missing: %s%n", missingMoves));
			message.append(String.format(" Invalid: %s%n", invalidMoves));
		}

		return message.toString();
	}
}
