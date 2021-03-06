package org.sagebionetworks.research.sageresearch.extensions

import org.sagebionetworks.research.sageresearch.dao.room.ScheduledActivityEntity
import org.threeten.bp.Duration
import org.threeten.bp.LocalDateTime

//
//  Copyright © 2018 Sage Bionetworks. All rights reserved.
//
// Redistribution and use in source and binary forms, with or without modification,
// are permitted provided that the following conditions are met:
//
// 1.  Redistributions of source code must retain the above copyright notice, this
// list of conditions and the following disclaimer.
//
// 2.  Redistributions in binary form must reproduce the above copyright notice,
// this list of conditions and the following disclaimer in the documentation and/or
// other materials provided with the distribution.
//
// 3.  Neither the name of the copyright holder(s) nor the names of any contributors
// may be used to endorse or promote products derived from this software without
// specific prior written permission. No license is granted to the trademarks of
// the copyright holders even if such marks are included in this software.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
// FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
// DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
// CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
// OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//

/**
 * Extension function for lists of ScheduledActivityEntity that filters a list on an activity identifier
 * @param activityId list will be filtered on schedules that have this activity identifier
 * @return a new instance of a list that has the filtered schedules
 */
fun List<ScheduledActivityEntity>.filterByActivityId(activityId: String): List<ScheduledActivityEntity> {
    return ArrayList(this).filter {
        it.activity != null &&
                (activityId == it.activity?.task?.identifier ||
                activityId == it.activity?.survey?.identifier ||
                activityId == it.activity?.compoundActivity?.taskIdentifier)
    }
}

/**
 * @return list of schedules that are available today
 */
fun List<ScheduledActivityEntity>.availableToday(): List<ScheduledActivityEntity>? {
    val todayStart = LocalDateTime.now().startOfDay()
    val todayEnd = LocalDateTime.now().endOfDay()
    return this.filter {
        val scheduleOn = it.scheduledOn
        val expiresOn = it.expiresOn
        scheduleOn != null &&
                ((expiresOn != null && scheduleOn.isBefore(todayEnd) && expiresOn.isAfter(todayStart)) ||
                        (expiresOn == null && scheduleOn.isBefore(todayEnd)))
    }
}

/**
 * @return the schedule that is closest to now
 */
fun List<ScheduledActivityEntity>.scheduleClosestToNow(): ScheduledActivityEntity? {
    val now = LocalDateTime.now()
    return this.sortedWith(kotlin.Comparator { s1, s2 ->
        Duration.between(now, s1.scheduledOn).nano
                .compareTo(Duration.between(now, s2.scheduledOn).nano)
    }).firstOrNull()
}
