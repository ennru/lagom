/*
 * Copyright (C) 2016-2019 Lightbend Inc. <https://www.lightbend.com>
 */

package com.lightbend.lagom.scaladsl.persistence

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.lightbend.lagom.internal.persistence.ReadSideConfig
import com.lightbend.lagom.internal.scaladsl.persistence.ReadSideImpl
import com.lightbend.lagom.scaladsl.cluster.ClusterComponents
import com.lightbend.lagom.scaladsl.cluster.projections.ProjectorComponents
import com.lightbend.lagom.scaladsl.cluster.projections.ProjectorRegistry
import play.api.Configuration

import scala.concurrent.ExecutionContext

/**
 * Persistence components (for compile-time injection).
 */
trait PersistenceComponents extends ReadSidePersistenceComponents

/**
 * Write-side persistence components (for compile-time injection).
 */
trait WriteSidePersistenceComponents extends ClusterComponents {
  def persistentEntityRegistry: PersistentEntityRegistry
}

/**
 * Read-side persistence components (for compile-time injection).
 */
trait ReadSidePersistenceComponents extends WriteSidePersistenceComponents with ProjectorComponents {
  def actorSystem: ActorSystem
  def executionContext: ExecutionContext
  def materializer: Materializer
  def projectorRegistry: ProjectorRegistry

  def configuration: Configuration

  lazy val readSideConfig: ReadSideConfig = ReadSideConfig(
    configuration.underlying.getConfig("lagom.persistence.read-side")
  )
  lazy val readSide: ReadSide =
    new ReadSideImpl(actorSystem, readSideConfig, persistentEntityRegistry, projectorRegistryImpl, None)(
      executionContext,
      materializer
    )

}
